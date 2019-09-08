/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.future.SimpleFutureDel;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.method.SimpleMethodDel;
import com.hktcode.bgsimple.method.SimpleMethodDelParamsDefault;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicReference;

public class Uppdc implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(Uppdc.class);

    public static Uppdc of //
        /* */(UpperProducerConfig config //
        /* */, BlockingQueue<UpperProducerRecord> getout //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (getout == null) {
            throw new ArgumentNullException("getout");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new Uppdc(config, getout, status);
    }

    private final UpperProducerConfig config;

    private final BlockingQueue<UpperProducerRecord> getout;

    private final AtomicReference<SimpleStatus> status;

    private Uppdc //
        /* */( UpperProducerConfig config //
        /* */, BlockingQueue<UpperProducerRecord> getout //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        this.config = config;
        this.getout = getout;
        this.status = status;
    }

    public void runWithInterrupted() throws InterruptedException, ScriptException
    {
        UppdcAction action = UppdcActionRun.of(config, getout, status);
        try {
            do {
                UppdcActionRun act = (UppdcActionRun)action;
                action = act.next();
            } while (action instanceof UppdcActionRun);
            logger.info("upper producer completes");
        }
        catch (InterruptedException ex) {
            throw ex;
        }
        catch (Exception ex) {
            logger.error("upper producer throws exception: ", ex);
            UppdcActionErr erract = action.next(ex);
            SimpleStatusInner o;
            SimpleStatus f;
            do {
                o = erract.newStatus(erract);
                f = o;
                if (o instanceof SimpleStatusInnerRun) {
                    SimpleMethodDel[] method = new SimpleMethodDel[] {
                        SimpleMethodDelParamsDefault.of(),
                        SimpleMethodDelParamsDefault.of(),
                        action.del()
                    };
                    Phaser phaser = new Phaser(3);
                    f = SimpleStatusOuterDel.of(method, phaser);
                }
                else if (!isSameEnd(erract, (SimpleStatusInnerEnd)o)){
                    SimpleStatusInnerEnd end = (SimpleStatusInnerEnd)o;
                    SimpleMethodAllResultEnd[] method = new SimpleMethodAllResultEnd[] {
                        end.result.get(0),
                        end.result.get(1),
                        (SimpleMethodAllResultEnd) erract.del()
                    };
                    f = SimpleStatusInnerEnd.of(ImmutableList.copyOf(method));
                }
            } while (o != f && !this.status.compareAndSet(o, f));
            if (f instanceof SimpleStatusOuterDel) {
                SimpleStatusOuterDel del = (SimpleStatusOuterDel)f;
                SimpleFutureDel future = SimpleFutureDel.of(status, del);
                future.get();
            }
        }
        logger.info("upper producer terminate");
    }

    private static boolean isSameEnd(UppdcActionErr erract, SimpleStatusInnerEnd status)
    {
        SimpleMethodAllResultEnd statusResult = status.result.get(1);
        if (!(statusResult instanceof UppdcResultErr)) {
            return false;
        }
        UppdcResultErr rhs = (UppdcResultErr)statusResult;
        return rhs.metric == erract.metric && rhs.config == erract.config;
    }

    @Override
    public void run()
    {
        try {
            this.runWithInterrupted();
        }
        catch (InterruptedException ex) {
            logger.error("should never happen", ex);
            Thread.currentThread().interrupt();
        }
        catch (ScriptException ex) {
            logger.error("should never happen", ex);
        }
    }
}

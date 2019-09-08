/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.junction;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.future.SimpleFutureDel;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.method.SimpleMethodDel;
import com.hktcode.bgsimple.method.SimpleMethodDelParamsDefault;
import com.hktcode.bgsimple.status.*;
import com.hktcode.bgsimple.triple.TripleJunctionConfig;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.UpperProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicReference;

public class Upjct implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(Upjct.class);

    public static Upjct of //
        /* */( TripleJunctionConfig config //
        /* */, BlockingQueue<UpperConsumerRecord> comein //
        /* */, BlockingQueue<UpperProducerRecord> getout //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (comein == null) {
            throw new ArgumentNullException("comein");
        }
        if (getout == null) {
            throw new ArgumentNullException("getout");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new Upjct(config, comein, getout, status);
    }

    private final TripleJunctionConfig config;

    private final BlockingQueue<UpperConsumerRecord> comein;

    private final BlockingQueue<UpperProducerRecord> getout;

    private final AtomicReference<SimpleStatus> status;

    private Upjct //
        /* */( TripleJunctionConfig config //
        /* */, BlockingQueue<UpperConsumerRecord> comein //
        /* */, BlockingQueue<UpperProducerRecord> getout //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        this.config = config;
        this.comein = comein;
        this.getout = getout;
        this.status = status;
    }

    public void runWithInterrupted() throws InterruptedException, ScriptException
    {
        UpjctAction action = UpjctActionRun.of(config, comein, getout, status);
        try {
            do {
                UpjctActionRun act = (UpjctActionRun)action;
                action = act.next();
            } while (action instanceof UpjctActionRun);
            logger.info("uper junction completes");
        }
        catch (InterruptedException ex) {
            throw ex;
        }
        catch (Exception ex) {
            logger.error("upper junction throws exception: ", ex);
            UpjctActionErr erract = action.next(ex);
            SimpleStatusInner o;
            SimpleStatus f;
            do {
                o = erract.newStatus(erract);
                f = o;
                if (o instanceof SimpleStatusInnerRun) {
                    SimpleMethodDel[] method = new SimpleMethodDel[] {
                        SimpleMethodDelParamsDefault.of(),
                        action.del(),
                        SimpleMethodDelParamsDefault.of()
                    };
                    Phaser phaser = new Phaser(3);
                    f = SimpleStatusOuterDel.of(method, phaser);
                }
                else if (!isSameEnd(erract, (SimpleStatusInnerEnd)o)){
                    SimpleStatusInnerEnd end = (SimpleStatusInnerEnd)o;
                    SimpleMethodAllResultEnd[] method = new SimpleMethodAllResultEnd[] {
                        end.result.get(0),
                        (SimpleMethodAllResultEnd) erract.del(),
                        end.result.get(2)
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
        logger.info("upper junction finish");
    }

    private static boolean isSameEnd(UpjctActionErr erract, SimpleStatusInnerEnd status)
    {
        SimpleMethodAllResultEnd statusResult = status.result.get(1);
        if (!(statusResult instanceof UpjctResultErr)) {
            return false;
        }
        UpjctResultErr rhs = (UpjctResultErr)statusResult;
        return rhs.metric == erract.metric && rhs.config == erract.config;
    }

    @Override
    public void run()
    {
        try {
            this.runWithInterrupted();
        } catch (InterruptedException e) {
            logger.error("should never happen", e);
            Thread.currentThread().interrupt();
        } catch (ScriptException e) {
            logger.error("should never happen", e);
        }
    }
}

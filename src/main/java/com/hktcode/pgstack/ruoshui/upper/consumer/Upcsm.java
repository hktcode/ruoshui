/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.future.SimpleFutureDel;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.method.SimpleMethodDel;
import com.hktcode.bgsimple.method.SimpleMethodDelParamsDefault;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgConfigMainline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicReference;

public class Upcsm implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(Upcsm.class);

    public static Upcsm of //
        /* */(PgConfigMainline config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, BlockingQueue<UpperRecordConsumer> comein //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (comein == null) {
            throw new ArgumentNullException("getout");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new Upcsm(config, comein, status);
    }

    private final PgConfigMainline config;

    private final BlockingQueue<UpperRecordConsumer> comein;

    private final AtomicReference<SimpleStatus> status;

    private Upcsm //
        /* */(PgConfigMainline config //
        /* */, BlockingQueue<UpperRecordConsumer> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        this.config = config;
        this.comein = comein;
        this.status = status;
    }

    public void runWithInterrupted() throws InterruptedException, ScriptException
    {
        UpcsmAction action = UpcsmActionRun.of(config, comein, status);
        try {
            do {
                UpcsmActionRun act = (UpcsmActionRun)action;
                action = act.next();
            } while (action instanceof UpcsmActionRun);
            logger.info("upper consumer completes");
            return;
        }
        catch (InterruptedException ex) {
            throw ex;
        }
        catch (FetchThreadThrowsErrorException ex) {
            logger.info("upper consumer throws FetchThreadThrowsErrorException");
            action = action.next(ex);
        }
        catch (Exception ex) {
            logger.error("upper consumer throws exception: ", ex);
            action = action.next(ex);
        }
        UpcsmActionErr erract = (UpcsmActionErr) action;
        SimpleStatusInner o;
        SimpleStatus f;
        do {
            o = erract.newStatus(erract);
            f = o;
            if (o instanceof SimpleStatusInnerRun) {
                SimpleMethodDel[] method = new SimpleMethodDel[] {
                    action.del(),
                    SimpleMethodDelParamsDefault.of(),
                    SimpleMethodDelParamsDefault.of()
                };
                Phaser phaser = new Phaser(3);
                f = SimpleStatusOuterDel.of(method, phaser);
            }
            else if (!isSameEnd(erract, (SimpleStatusInnerEnd)o)) {
                SimpleStatusInnerEnd end = (SimpleStatusInnerEnd)o;
                SimpleMethodAllResultEnd[] method = new SimpleMethodAllResultEnd[] {
                    (SimpleMethodAllResultEnd)action.del(),
                    end.result.get(1),
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
        logger.info("upper consumer terminate.");
    }

    private static boolean isSameEnd(UpcsmActionErr erract, SimpleStatusInnerEnd status)
    {
        SimpleMethodAllResultEnd statusResult = status.result.get(1);
        if (!(statusResult instanceof UpcsmResultErr)) {
            return false;
        }
        UpcsmResultErr rhs = (UpcsmResultErr)statusResult;
        return rhs.metric == erract.metric;// && rhs.config == erract.config;
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

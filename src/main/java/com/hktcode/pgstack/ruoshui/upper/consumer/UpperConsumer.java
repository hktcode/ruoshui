/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.future.SimpleFutureDel;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.RunnableWithInterrupted;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicReference;

public class UpperConsumer implements RunnableWithInterrupted
{
    private static final Logger logger = LoggerFactory.getLogger(UpperConsumer.class);

    public static UpperConsumer of //
        /* */( MainlineConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, BlockingQueue<UpperConsumerRecord> comein //
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
        return new UpperConsumer(config, comein, status);
    }

    private final MainlineConfig config;

    private final BlockingQueue<UpperConsumerRecord> comein;

    private final AtomicReference<SimpleStatus> status;

    private UpperConsumer //
        /* */( MainlineConfig config //
        /* */, BlockingQueue<UpperConsumerRecord> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        this.config = config;
        this.comein = comein;
        this.status = status;
    }

    @Override
    public void runWithInterrupted() throws InterruptedException
    {
        UpperConsumerAction action //
            = UpperConsumerActionRun.of(config, comein, status);
        try {
            do {
                UpperConsumerActionRun act = (UpperConsumerActionRun)action;
                action = act.next();
            } while (action instanceof UpperConsumerActionRun);
            logger.info("upper consumer completes");
        }
        catch (InterruptedException ex) {
            throw ex;
        }
        catch (Exception ex) {
            logger.error("upper consumer throws exception: ", ex);
            UpperConsumerActionErr erract = action.next(ex);
            SimpleStatusInner o;
            SimpleStatus f;
            do {
                o = action.newStatus(action);
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
                else {
                    SimpleStatusInnerEnd end = (SimpleStatusInnerEnd)o;
                    if (erract.metric != ((UpperConsumerResultErr)end.result.get(0)).metric) {
                        SimpleMethodAllResultEnd[] method = new SimpleMethodAllResultEnd[] {
                            (SimpleMethodAllResultEnd)action.del(),
                            end.result.get(1),
                            end.result.get(2)
                        };
                        f = SimpleStatusInnerEnd.of(ImmutableList.copyOf(method));
                    }
                }
            } while (o != f && !this.status.compareAndSet(o, f));
            if (f instanceof SimpleStatusOuterDel) {
                SimpleStatusOuterDel del = (SimpleStatusOuterDel)f;
                SimpleFutureDel future = SimpleFutureDel.of(status, del);
                future.get();
            }
        }
        logger.info("upper consumer finish");
    }
}

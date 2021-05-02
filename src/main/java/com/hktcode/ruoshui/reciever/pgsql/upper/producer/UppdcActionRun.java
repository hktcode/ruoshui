/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleAction;
import com.hktcode.simple.SimpleActionRun;
import com.hktcode.simple.SimpleFinish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class UppdcActionRun<C extends UppdcConfig, M extends UppdcMetric>
        implements SimpleActionRun<C, M, UpperExesvc>
{
    private static final Logger logger = LoggerFactory.getLogger(UppdcActionRun.class);

    protected UppdcActionRun()
    {
    }

    @Override
    public SimpleAction next(C config, M metric, UpperExesvc exesvc) ///
            throws Throwable
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        final Tqueue<UpperRecordProducer> getout = exesvc.tgtqueue;
        try (UppdcSender sender = this.sender(config, metric)) {
            UpperRecordProducer d = null;
            Throwable ex;
            while (exesvc.run(metric).deletets == Long.MAX_VALUE) {
                if ((ex = metric.callbackRef.get()) != null) {
                    logger.error("callback throws exception", ex);
                    throw ex;
                } else if (d == null) {
                    d = getout.poll();
                } else {
                    sender.send(d);
                    d = null;
                }
            }
        }
        return SimpleFinish.of();
    }

    protected abstract UppdcSender sender(C config, M metric) throws IOException;
}

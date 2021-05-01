/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperHolder;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleAction;
import com.hktcode.simple.SimpleActionEnd;
import com.hktcode.simple.SimpleActionRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

abstract class UppdcActionRun<C extends UppdcConfig, M extends UppdcMetric>
        extends SimpleActionRun<C, M, UpperHolder>
{
    private static final Logger logger = LoggerFactory.getLogger(UppdcActionRun.class);

    protected UppdcActionRun(C config, M metric, UpperHolder holder)
    {
        super(config, metric, holder);
    }

    @Override
    public SimpleAction next() throws Throwable
    {
        final Tqueue<UpperRecordProducer> getout = this.entity.tgtqueue;
        try (UppdcSender<?, ?> sender = this.sender()) {
            UpperRecordProducer d = null;
            Throwable ex;
            while (this.entity.run(metric).deletets == Long.MAX_VALUE) {
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
        return SimpleActionEnd.of(this.config, this.metric, this.entity);
    }

    protected abstract UppdcSender<?, ?> sender() throws IOException;
}

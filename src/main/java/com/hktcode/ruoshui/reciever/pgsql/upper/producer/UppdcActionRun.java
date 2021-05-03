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

public abstract class UppdcActionRun implements SimpleActionRun<UppdcMeters, UpperExesvc>
{
    private static final Logger logger = LoggerFactory.getLogger(UppdcActionRun.class);

    protected UppdcActionRun()
    {
    }

    @Override
    public SimpleAction next(UppdcMeters meters, UpperExesvc exesvc) ///
            throws Throwable
    {
        if (meters == null) {
            throw new ArgumentNullException("meters");
        }
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        try (UppdcSender sender = this.sender()) {
            final Tqueue<UpperRecordProducer> getout = exesvc.tgtqueue;
            UpperRecordProducer d = null;
            Throwable ex;
            while (exesvc.run(meters).deletets == Long.MAX_VALUE) {
                if ((ex = meters.callbackRef.get()) != null) {
                    logger.error("callback throws exception", ex);
                    throw ex;
                } else if (d == null) {
                    d = getout.poll();
                } else {
                    sender.send(meters, d);
                    d = null;
                }
            }
        }
        return SimpleFinish.of();
    }

    protected abstract UppdcSender sender() throws IOException;
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleWkstep;
import com.hktcode.simple.SimpleWkstepAction;
import com.hktcode.simple.SimpleWkstepTheEnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class UppdcWkstepAction implements SimpleWkstepAction<UppdcWorkerMeters, UpperExesvc>
{
    private static final Logger logger = LoggerFactory.getLogger(UppdcWkstepAction.class);

    protected UppdcWkstepAction()
    {
    }

    @Override
    public SimpleWkstep next(UppdcWorkerMeters meters, UpperExesvc exesvc) ///
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
        return SimpleWkstepTheEnd.of();
    }

    protected abstract UppdcSender sender() throws IOException;
}

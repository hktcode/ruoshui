/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWkstep;
import com.hktcode.simple.SimpleWkstepAction;
import com.hktcode.simple.SimpleWkstepTheEnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UppdcWkstepAction implements SimpleWkstepAction<UppdcWorkerArgval, UppdcWorkerGauges>
{
    private static final Logger logger = LoggerFactory.getLogger(UppdcWkstepAction.class);

    public static UppdcWkstepAction of(Tqueue<UpperRecordProducer> target)
    {
        if (target == null) {
            throw new ArgumentNullException("target");
        }
        return new UppdcWkstepAction(target);
    }

    private UppdcWkstepAction(Tqueue<UpperRecordProducer> target)
    {
        this.target = target;
    }

    private final Tqueue<UpperRecordProducer> target;

    @Override
    public SimpleWkstep next(UppdcWorkerArgval argval, UppdcWorkerGauges meters, SimpleAtomic holder) ///
            throws Throwable
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (meters == null) {
            throw new ArgumentNullException("meters");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        UppdcWkstepArgval params = argval.actionInfos.get(0);
        try (UppdcSender sender = params.sender()) {
            UpperRecordProducer d = null;
            Throwable ex;
            while (holder.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                if ((ex = meters.callbackRef.get()) != null) {
                    logger.error("callback throws exception", ex);
                    throw ex;
                } else if (d == null) {
                    d = this.target.poll();
                } else {
                    sender.send(meters, d);
                    d = null;
                }
            }
        }
        return SimpleWkstepTheEnd.of();
    }
}

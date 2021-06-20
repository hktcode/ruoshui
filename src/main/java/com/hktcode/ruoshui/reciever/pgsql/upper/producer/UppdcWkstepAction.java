/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWkstep;
import com.hktcode.simple.SimpleWkstepAction;
import com.hktcode.simple.SimpleWkstepTheEnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class UppdcWkstepAction implements SimpleWkstepAction<UppdcWorkerArgval, UppdcWorkerGauges>
{
    private static final Logger logger = LoggerFactory.getLogger(UppdcWkstepAction.class);

    public static UppdcWkstepAction of()
    {
        return new UppdcWkstepAction();
    }

    private UppdcWkstepAction()
    {
    }

    @Override
    public SimpleWkstep next(UppdcWorkerArgval argval, UppdcWorkerGauges gauges, SimpleAtomic atomic) ///
            throws Throwable
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (gauges == null) {
            throw new ArgumentNullException("gauges");
        }
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        UppdcWkstepArgval a = argval.actionInfos.get(0);
        UppdcWkstepGauges g = UppdcWkstepGauges.of();
        gauges.wkstep.add(g);
        Throwable ex;
        List<UpperRecordProducer> lhs, rhs = gauges.fetchMetric.list();
        int spins = 0;
        long now, logtime = System.currentTimeMillis();
        Iterator<UpperRecordProducer> iter = rhs.iterator();
        try (UppdcSender sender = a.sender()) {
            while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                long logDuration = a.logDuration;
                if ((ex = gauges.callbackRef.get()) != null) {
                    logger.error("callback throws exception", ex);
                    throw ex;
                } else if (iter.hasNext()) {
                    // 未来计划：send方法支持数组，发送多个记录，提高性能
                    sender.send(gauges, iter.next());
                } else if ((lhs = gauges.fetchMetric.poll(rhs)) != rhs) {
                    rhs = lhs;
                    iter = rhs.iterator();
                } else if (logtime + logDuration >= (now = System.currentTimeMillis())) {
                    logger.info("write to logDuration={}", logDuration);
                    logtime = now;
                } else {
                    gauges.spinsMetric.spins(spins++);
                }

            }
        }
        return SimpleWkstepTheEnd.of();
    }
}

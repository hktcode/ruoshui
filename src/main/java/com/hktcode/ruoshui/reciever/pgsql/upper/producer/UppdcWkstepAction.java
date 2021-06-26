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
        List<UpperRecordProducer> lhs, rhs = argval.recver.list();
        long now, prelog = System.currentTimeMillis(), spins = 0;
        Iterator<UpperRecordProducer> iter = rhs.iterator();
        try (UppdcSender.Client client = argval.sender.client()) {
            while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                long l = argval.xspins.logDuration;
                if (iter.hasNext()) {
                    // 未来计划：send方法支持数组，发送多个记录，提高性能
                    client.send(iter.next());
                } else if ((lhs = gauges.recver.poll(rhs)) != rhs) {
                    rhs = lhs;
                    iter = rhs.iterator();
                } else if (prelog + l >= (now = System.currentTimeMillis())) {
                    logger.info("write to logDuration={}", l);
                    prelog = now;
                } else {
                    gauges.xspins.spins(spins++);
                }
            }
        }
        return SimpleWkstepTheEnd.of();
    }
}

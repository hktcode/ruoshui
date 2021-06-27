/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWkstep;
import com.hktcode.simple.SimpleWkstepAction;
import com.hktcode.simple.SimpleWkstepTheEnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpcsmWkstepAction implements SimpleWkstepAction<UpcsmWorkerArgval, UpcsmWorkerArgval>
{
    private static final Logger logger = LoggerFactory.getLogger(UpcsmWkstepAction.class);

    public static UpcsmWkstepAction of()
    {
        return new UpcsmWkstepAction();
    }

    @Override
    public SimpleWkstep next(UpcsmWorkerArgval argval, UpcsmWorkerArgval gauges, SimpleAtomic atomic) //
            throws InterruptedException, SQLException
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
        UpperRecordConsumer r;
        int curCapacity = argval.sender.maxCapacity;
        List<UpperRecordConsumer> rhs, lhs = new ArrayList<>(curCapacity);
        int spins = 0, spinsStatus = Xqueue.Spins.RESET;
        long now, logtime = System.currentTimeMillis();
        final Xqueue.Offer<UpperRecordConsumer> sender = gauges.sender.offerXqueue();
        try (UpcsmRecverArgval.Client client = argval.recver.client()) {
            while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                // 未来计划：此处可以提高性能
                int size = lhs.size();
                int capacity = argval.sender.maxCapacity;
                long logDuration = argval.xspins.logDuration;
                if (    (size > 0)
                     // 未来计划：支持bufferCount和maxDuration
                     && (rhs = sender.push(lhs)) != lhs
                     && (curCapacity != capacity || (lhs = rhs) == null)
                ) {
                    lhs = new ArrayList<>(capacity);
                    curCapacity = capacity;
                    spins = 0;
                    logtime = System.currentTimeMillis();
                } else if (size < capacity && (r = client.recv()) != null) {
                    lhs.add(r);
                    spins = 0;
                    logtime = System.currentTimeMillis();
                } else if (logtime + logDuration >= (now = System.currentTimeMillis())) {
                    logger.info("logDuration={}", logDuration);
                    logtime = now;
                } else {
                    if (spinsStatus == Xqueue.Spins.SLEEP) {
                        client.forceUpdateStatus();
                    }
                    spinsStatus = gauges.xspins.spins(spins++);
                }
            }
        }
        logger.info("pgsender complete");
        gauges.finish = System.currentTimeMillis();
        return SimpleWkstepTheEnd.of();
    }

    private UpcsmWkstepAction()
    {
    }
}

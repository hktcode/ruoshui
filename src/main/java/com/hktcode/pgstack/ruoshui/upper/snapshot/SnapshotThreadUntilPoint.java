/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TransferQueue;

public class SnapshotThreadUntilPoint extends SnapshotThread
{
    private static final Logger logger //
        = LoggerFactory.getLogger(SnapshotThreadUntilPoint.class);

    public static SnapshotThreadUntilPoint of
        /* */( PgReplSlotTuple slot
        /* */, MainlineThread xact
        /* */, Thread thread
        /* */, TransferQueue<SnapshotRecord> tqueue
        /* */)
    {
        if (slot == null) {
            throw new ArgumentNullException("slot");
        }
        if (xact == null) {
            throw new ArgumentNullException("xact");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        return new SnapshotThreadUntilPoint(slot, xact, thread, tqueue);
    }

    private final PgReplSlotTuple slot;

    private SnapshotThreadUntilPoint
        /* */( PgReplSlotTuple slot
        /* */, MainlineThread xact
        /* */, Thread thread
        /* */, TransferQueue<SnapshotRecord> tqueue
        /* */)
    {
        super(thread, tqueue, xact);
        this.slot = slot;
    }

    @Override
    public UpperConsumerRecord poll(long timeout, UpperConsumerMetric metric) throws InterruptedException
    {
        UpperConsumerRecord record = xact.poll(timeout);
        if (record != null) {
            if (record.msg instanceof LogicalTxactBeginsMsg) {
                LogicalTxactBeginsMsg beginsMsg = (LogicalTxactBeginsMsg)record.msg;
                if (Long.compareUnsigned(beginsMsg.lsnofcmt, slot.consistentPoint) > 0) {
                    metric.fetchThread = SnapshotThreadSelectData.of(record, xact, thread, tqueue);
                    return null;
                }
            }
            return record;
        }
        if (!thread.isAlive()) {
            logger.error("snapshot post is not alive."); // TODO:
            // metric.fetchThread = xact;
        }
        return null;
    }
}

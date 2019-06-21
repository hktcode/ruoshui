/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.post;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.txaction.UpperTxactionThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TransferQueue;

public class UpperSnapshotPostThreadUntilPoint extends UpperSnapshotPostThread
{
    private static final Logger logger //
        = LoggerFactory.getLogger(UpperSnapshotPostThreadUntilPoint.class);

    public static UpperSnapshotPostThreadUntilPoint of
        /* */( PgReplSlotTuple slot
        /* */, UpperTxactionThread xact
        /* */, Thread thread
        /* */, TransferQueue<UpperSnapshotPostRecord> tqueue
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
        return new UpperSnapshotPostThreadUntilPoint(slot, xact, thread, tqueue);
    }

    private final PgReplSlotTuple slot;

    private UpperSnapshotPostThreadUntilPoint
        /* */( PgReplSlotTuple slot
        /* */, UpperTxactionThread xact
        /* */, Thread thread
        /* */, TransferQueue<UpperSnapshotPostRecord> tqueue
        /* */)
    {
        super(thread, tqueue, xact);
        this.slot = slot;
    }

    @Override
    public UpperConsumerRecord poll(long timeout, UpperConsumerMutableMetric metric) throws InterruptedException
    {
        UpperConsumerRecord record = xact.poll(timeout, metric);
        if (record != null) {
            if (record.msg instanceof LogicalTxactBeginsMsg) {
                LogicalTxactBeginsMsg beginsMsg = (LogicalTxactBeginsMsg)record.msg;
                if (Long.compareUnsigned(beginsMsg.lsnofcmt, slot.consistentPoint) > 0) {
                    metric.fetchThread = UpperSnapshotPostThreadSelectData.of(record, xact, thread, tqueue);
                    return null;
                }
            }
            return record;
        }
        if (!thread.isAlive()) {
            logger.error("snapshot post is not alive."); // TODO:
            metric.fetchThread = xact;
        }
        return null;
    }
}

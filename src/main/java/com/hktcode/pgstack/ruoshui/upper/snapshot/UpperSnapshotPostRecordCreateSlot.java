/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThread;

import java.util.concurrent.TransferQueue;

public class UpperSnapshotPostRecordCreateSlot implements UpperSnapshotPostRecord
{
    public static UpperSnapshotPostRecordCreateSlot of(PgReplSlotTuple slotTuple)
    {
        if (slotTuple == null) {
            throw new ArgumentNullException("slotTuple");
        }
        return new UpperSnapshotPostRecordCreateSlot(slotTuple);
    }

    public final PgReplSlotTuple slotTuple;

    @Override
    public UpperConsumerRecord update
        /* */( UpperConsumerMutableMetric metric
        /* */, Thread thread
        /* */, TransferQueue<UpperSnapshotPostRecord> tqueue
        /* */, MainlineThread xact
        /* */)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        metric.fetchThread = UpperSnapshotPostThreadUntilPoint.of(slotTuple, xact, thread, tqueue);
        return null;
    }

    private UpperSnapshotPostRecordCreateSlot(PgReplSlotTuple slotTuple)
    {
        this.slotTuple = slotTuple;
    }
}

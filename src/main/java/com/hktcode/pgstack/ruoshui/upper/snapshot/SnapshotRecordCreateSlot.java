/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThreadWork;

import java.util.concurrent.TransferQueue;

public class SnapshotRecordCreateSlot implements SnapshotRecord
{
    public static SnapshotRecordCreateSlot of(PgReplSlotTuple slotTuple)
    {
        if (slotTuple == null) {
            throw new ArgumentNullException("slotTuple");
        }
        return new SnapshotRecordCreateSlot(slotTuple);
    }

    public final PgReplSlotTuple slotTuple;

    @Override
    public UpperConsumerRecord update
        /* */( UpperConsumerMetric metric
        /* */, Thread thread
        /* */, TransferQueue<SnapshotRecord> tqueue
        /* */, MainlineThreadWork xact
        /* */)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        metric.fetchThread = SnapshotThreadUntilPoint.of(slotTuple, xact, thread, tqueue);
        return null;
    }

    private SnapshotRecordCreateSlot(PgReplSlotTuple slotTuple)
    {
        this.slotTuple = slotTuple;
    }
}

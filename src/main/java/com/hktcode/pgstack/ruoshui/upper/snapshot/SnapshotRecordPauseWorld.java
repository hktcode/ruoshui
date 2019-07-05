/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThreadWork;

import java.util.concurrent.TransferQueue;

public class SnapshotRecordPauseWorld implements SnapshotRecord
{
    public static SnapshotRecordPauseWorld of()
    {
        return new SnapshotRecordPauseWorld();
    }

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
        metric.fetchThread = SnapshotThreadCreateSlot.of(thread, tqueue, xact);
        return null;
    }

    private SnapshotRecordPauseWorld()
    {
    }
}

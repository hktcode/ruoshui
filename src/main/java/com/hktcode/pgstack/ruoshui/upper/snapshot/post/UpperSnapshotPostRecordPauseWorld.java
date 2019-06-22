/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.post;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.txaction.UpperTxactionThread;

import java.util.concurrent.TransferQueue;

public class UpperSnapshotPostRecordPauseWorld implements UpperSnapshotPostRecord
{
    public static UpperSnapshotPostRecordPauseWorld of()
    {
        return new UpperSnapshotPostRecordPauseWorld();
    }

    @Override
    public UpperConsumerRecord update
        /* */( UpperConsumerMutableMetric metric
        /* */, Thread thread
        /* */, TransferQueue<UpperSnapshotPostRecord> tqueue
        /* */, UpperTxactionThread xact
        /* */)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        metric.fetchThread = UpperSnapshotPostThreadCreateSlot.of(thread, tqueue, xact);
        return null;
    }

    private UpperSnapshotPostRecordPauseWorld()
    {
    }
}

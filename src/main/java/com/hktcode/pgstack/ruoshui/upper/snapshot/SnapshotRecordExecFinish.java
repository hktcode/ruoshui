/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThreadWork;

import java.util.concurrent.TransferQueue;

public class SnapshotRecordExecFinish implements SnapshotRecord
{
    public static SnapshotRecordExecFinish of()
    {
        return new SnapshotRecordExecFinish();
    }

    @Override
    public UpperConsumerRecord update //
        /* */(UpperConsumerMutableMetric metric //
        /* */, Thread thread //
        /* */, TransferQueue<SnapshotRecord> tqueue //
        /* */, MainlineThreadWork xact //
        /* */)
    {
        metric.fetchThread = xact;
        return null;
    }

    private SnapshotRecordExecFinish()
    {
    }
}

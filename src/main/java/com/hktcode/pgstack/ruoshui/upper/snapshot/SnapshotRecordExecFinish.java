/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.pgstack.ruoshui.upper.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThread;

import java.util.concurrent.TransferQueue;

public class SnapshotRecordExecFinish implements SnapshotRecord
{
    public static SnapshotRecordExecFinish of()
    {
        return new SnapshotRecordExecFinish();
    }

    @Override
    public UpperConsumerRecord update //
        /* */(UpperConsumerMetric metric //
        /* */, Thread thread //
        /* */, TransferQueue<SnapshotRecord> tqueue //
        /* */, MainlineThread xact //
        /* */)
    {
        // metric.fetchThread = xact;
        return null;
    }

    private SnapshotRecordExecFinish()
    {
    }
}

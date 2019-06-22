/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.post;

import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.txaction.UpperTxactionThread;

import java.util.concurrent.TransferQueue;

public class UpperSnapshotPostRecordExecFinish implements UpperSnapshotPostRecord
{
    public static UpperSnapshotPostRecordExecFinish of()
    {
        return new UpperSnapshotPostRecordExecFinish();
    }

    @Override
    public UpperConsumerRecord update //
        /* */(UpperConsumerMutableMetric metric //
        /* */, Thread thread //
        /* */, TransferQueue<UpperSnapshotPostRecord> tqueue //
        /* */, UpperTxactionThread xact //
        /* */)
    {
        metric.fetchThread = xact;
        return null;
    }

    private UpperSnapshotPostRecordExecFinish()
    {
    }
}

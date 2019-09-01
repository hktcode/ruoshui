/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThread;

import java.util.concurrent.TransferQueue;

public class SnapshotRecordLogicalMsg implements SnapshotRecord
{
    public static SnapshotRecordLogicalMsg of(long lsn, LogicalMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        return new SnapshotRecordLogicalMsg(lsn, msg);
    }

    public final long lsn;

    public final LogicalMsg msg;

    private SnapshotRecordLogicalMsg(long lsn, LogicalMsg msg)
    {
        this.lsn = lsn;
        this.msg = msg;
    }

    @Override
    public UpperConsumerRecord update
        /* */( UpperConsumerMetric metric
        /* */, Thread thread
        /* */, TransferQueue<SnapshotRecord> tqueue
        /* */, MainlineThread xact
        /* */)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        if (xact == null) {
            throw new ArgumentNullException("xact");
        }
        return UpperConsumerRecord.of(lsn, msg);
    }
}

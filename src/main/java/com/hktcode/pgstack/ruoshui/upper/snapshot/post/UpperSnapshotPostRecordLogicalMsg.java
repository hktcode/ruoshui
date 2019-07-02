/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.post;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThread;

import java.util.concurrent.TransferQueue;

public class UpperSnapshotPostRecordLogicalMsg implements UpperSnapshotPostRecord
{
    public static UpperSnapshotPostRecordLogicalMsg of(long lsn, LogicalMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        return new UpperSnapshotPostRecordLogicalMsg(lsn, msg);
    }

    public final long lsn;

    public final LogicalMsg msg;

    private UpperSnapshotPostRecordLogicalMsg(long lsn, LogicalMsg msg)
    {
        this.lsn = lsn;
        this.msg = msg;
    }

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

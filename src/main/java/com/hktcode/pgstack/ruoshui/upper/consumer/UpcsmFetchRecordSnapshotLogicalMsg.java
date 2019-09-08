/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpcsmFetchRecordSnapshotLogicalMsg implements UpcsmFetchRecordSnapshot
{
    public static UpcsmFetchRecordSnapshotLogicalMsg of(long lsn, LogicalMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        return new UpcsmFetchRecordSnapshotLogicalMsg(lsn, msg);
    }

    public final long lsn;

    public final LogicalMsg msg;

    private UpcsmFetchRecordSnapshotLogicalMsg(long lsn, LogicalMsg msg)
    {
        this.lsn = lsn;
        this.msg = msg;
    }

    @Override
    public UpperConsumerRecord toRecord(UpcsmActionRun action, UpcsmThreadSnapshot thread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        return UpperConsumerRecord.of(lsn, msg);
    }
}

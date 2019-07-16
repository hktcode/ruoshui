/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerSender;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperRunnableMetric;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotMetric;

import java.time.ZonedDateTime;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

class PgsqlTxactionSender extends UpperConsumerSender<PgsqlTxactionRecord, UpperRunnableMetric>
{
    static PgsqlTxactionSender of
        /* */( TransferQueue<PgsqlTxactionRecord> tqueue
        /* */, AtomicReference<SimpleStatus> status
        /* */)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new PgsqlTxactionSender(tqueue, status);
    }

    private PgsqlTxactionSender
        /* */( TransferQueue<PgsqlTxactionRecord> tqueue
        /* */, AtomicReference<SimpleStatus> status
        /* */)
    {
        super(tqueue, status);
    }

    @Override
    public void sendCreateSlot(PgReplSlotTuple slotTuple, long timeout, SnapshotMetric metric) //
    {
        if (slotTuple == null) {
            throw new ArgumentNullException("slotTuple");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
    }

    @Override
    public void sendExecFinish(long timeout, SnapshotMetric metric) //
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
    }

    @Override
    public void sendExecThrows(Throwable throwable, long timeout, SnapshotMetric metric) //
        throws InterruptedException
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        ZonedDateTime endtime = ZonedDateTime.now();
        super.push(PgsqlTxactionRecordThrows.of(endtime, throwable), timeout, timeout, metric);
    }

    @Override
    public void sendLogicalMsg(long lsn, LogicalMsg msg, long timeout, SnapshotMetric metric) //
        throws InterruptedException
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.push(PgsqlTxactionRecordNormal.of(lsn, msg), timeout, timeout, metric);
    }

    @Override
    public void sendPauseWorld(long timeout, SnapshotMetric metric) //
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
    }
}

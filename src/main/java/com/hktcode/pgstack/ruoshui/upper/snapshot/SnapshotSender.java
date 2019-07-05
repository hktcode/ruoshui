/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerSender;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperRunnableMetric;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class SnapshotSender extends UpperConsumerSender<SnapshotRecord, UpperRunnableMetric>
{
    public static SnapshotSender of
        /* */(TransferQueue<SnapshotRecord> tqueue
        /* */, AtomicReference<SimpleStatus> status
        /* */)
    {
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new SnapshotSender(tqueue, status);
    }

    private SnapshotSender
        /* */( TransferQueue<SnapshotRecord> tqueue
        /* */, AtomicReference<SimpleStatus> status
        /* */)
    {
        super(tqueue, status);
    }

    @Override
    public void sendCreateSlot(PgReplSlotTuple slotTuple, long timeout, SnapshotMetric metric) //
        throws InterruptedException
    {
        if (slotTuple == null) {
            throw new ArgumentNullException("slotTuple");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.send(SnapshotRecordCreateSlot.of(slotTuple), timeout, timeout, metric);
    }

    @Override
    public void sendExecFinish(long timeout, SnapshotMetric metric) throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.send(SnapshotRecordExecFinish.of(), timeout, timeout, metric);
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
        super.send(SnapshotRecordExecThrows.of(throwable), timeout, timeout, metric);
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
        super.send(SnapshotRecordLogicalMsg.of(lsn, msg), timeout, timeout, metric);
    }

    @Override
    public void sendPauseWorld(long timeout, SnapshotMetric metric) throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.push(SnapshotRecordPauseWorld.of(), timeout, timeout, metric);
    }
}

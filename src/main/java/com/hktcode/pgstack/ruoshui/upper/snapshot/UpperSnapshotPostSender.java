/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpperSnapshotPostSender //
    extends UpperSnapshotSender<UpperSnapshotPostRecord>
{
    public static UpperSnapshotPostSender of
        /* */( TransferQueue<UpperSnapshotPostRecord> tqueue
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status
        /* */)
    {
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new UpperSnapshotPostSender(tqueue, status);
    }

    private UpperSnapshotPostSender
        /* */( TransferQueue<UpperSnapshotPostRecord> tqueue
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status
        /* */)
    {
        super(tqueue, status);
    }

    @Override
    public void sendCreateSlot(PgReplSlotTuple slotTuple, long timeout, UpperSnapshotMetric metric) //
        throws InterruptedException
    {
        if (slotTuple == null) {
            throw new ArgumentNullException("slotTuple");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.send(UpperSnapshotPostRecordCreateSlot.of(slotTuple), timeout, timeout, metric);
    }

    @Override
    public void sendExecFinish(long timeout, UpperSnapshotMetric metric) throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.send(UpperSnapshotPostRecordExecFinish.of(), timeout, timeout, metric);
    }

    @Override
    public void sendExecThrows(Throwable throwable, long timeout, UpperSnapshotMetric metric) //
        throws InterruptedException
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.send(UpperSnapshotPostRecordExecThrows.of(throwable), timeout, timeout, metric);
    }

    @Override
    public void sendLogicalMsg(long lsn, LogicalMsg msg, long timeout, UpperSnapshotMetric metric) //
        throws InterruptedException
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.send(UpperSnapshotPostRecordLogicalMsg.of(lsn, msg), timeout, timeout, metric);
    }

    @Override
    public void sendPauseWorld(long timeout, UpperSnapshotMetric metric) throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.push(UpperSnapshotPostRecordPauseWorld.of(), timeout, timeout, metric);
    }
}

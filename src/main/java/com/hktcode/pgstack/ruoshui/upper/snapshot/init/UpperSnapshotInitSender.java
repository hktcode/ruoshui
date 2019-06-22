/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.init;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.snapshot.UpperSnapshotMetric;
import com.hktcode.pgstack.ruoshui.upper.snapshot.UpperSnapshotSender;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpperSnapshotInitSender //
    extends UpperSnapshotSender<UpperSnapshotInitRecord>
{
    public static UpperSnapshotInitSender of
        /* */( TransferQueue<UpperSnapshotInitRecord> tqueue
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status
        /* */)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new UpperSnapshotInitSender(tqueue, status);
    }

    private UpperSnapshotInitSender
        /* */( TransferQueue<UpperSnapshotInitRecord> tqueue
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
        super.send(UpperSnapshotInitRecordCreateSlot.of(), timeout, timeout, metric);
    }

    @Override
    public void sendExecFinish(long timeout, UpperSnapshotMetric metric) //
        throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.push(UpperSnapshotInitRecordExecFinish.of(), timeout, timeout, metric);
    }

    @Override
    public void sendExecThrows(Throwable throwable, long timeout, UpperSnapshotMetric metric) //) //
        throws InterruptedException
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.push(UpperSnapshotInitRecordExecThrows.of(throwable), timeout, timeout, metric);
    }

    @Override
    public void sendLogicalMsg(long lsn, LogicalMsg msg, long timeout, UpperSnapshotMetric metric) //) //
        throws InterruptedException
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.push(UpperSnapshotInitRecordLogicalMsg.of(lsn, msg), timeout, timeout, metric);
    }

    @Override
    public void sendPauseWorld(long timeout, UpperSnapshotMetric metric) //
        throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        super.push(UpperSnapshotInitRecordPauseWorld.of(), timeout, timeout, metric);
    }
}

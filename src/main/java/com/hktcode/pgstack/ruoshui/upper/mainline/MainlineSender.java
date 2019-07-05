/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerSender;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperRunnableMetric;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotMetric;

import java.time.ZonedDateTime;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

class MainlineSender extends UpperConsumerSender<MainlineRecord, UpperRunnableMetric>
{
    static MainlineSender of
        /* */( TransferQueue<MainlineRecord> tqueue
        /* */, AtomicReference<SimpleStatus> status
        /* */)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new MainlineSender(tqueue, status);
    }

    private MainlineSender
        /* */( TransferQueue<MainlineRecord> tqueue
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
        super.push(MainlineRecordThrows.of(endtime, throwable), timeout, timeout, metric);
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
        super.push(MainlineRecordNormal.of(lsn, msg), timeout, timeout, metric);
    }

    @Override
    public void sendPauseWorld(long timeout, SnapshotMetric metric) //
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
    }
}

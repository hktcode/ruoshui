/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.future.SimpleFutureDel;
import com.hktcode.bgsimple.future.SimpleFutureGet;
import com.hktcode.bgsimple.future.SimpleFuturePst;
import com.hktcode.bgsimple.future.SimpleFuturePut;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.pgsender.*;
import org.postgresql.replication.LogSequenceNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpcsmSenderMainline extends UpcsmSender
{
    public final List<PgResult> sslist = new ArrayList<>();

    public final TransferQueue<PgRecord> tqueue;

    public static UpcsmSenderMainline of(MainlineConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("thread");
        }
        TransferQueue<PgRecord> tqueue = new LinkedTransferQueue<>();
        SimpleMethodPut[] put = new SimpleMethodPut[] {
            SimpleMethodPutParamsDefault.of()
        };
        SimpleStatusOuterPut s = SimpleStatusOuterPut.of(put, new Phaser(2));
        AtomicReference<SimpleStatus> status = new AtomicReference<>(s);
        Thread thread = new Thread(Mainline.of(config, status, tqueue));
        return new UpcsmSenderMainline(thread, tqueue, status);
    }

    @Override
    public UpperRecordConsumer poll(long timeout, UpcsmActionRun action) //
        throws InterruptedException
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        PgRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            return record.toRecord(action, this);
        }
        else if (this.thread.isAlive()) {
            return null;
        }
        else {
            // TODO: throw new DelegateNotAliveException();
            throw new RuntimeException();
        }
    }

    private UpcsmSenderMainline //
        /* */(Thread thread //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) //
    {
        super(thread, status);
        this.tqueue = tqueue;
    }

    @Override
    public UpcsmReportSender put() throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleFuturePut future = holder.put();
        thread.start();
        PgResult mainline = (PgResult)future.get().get(0);
        ImmutableList<PgResult> snapshot = ImmutableList.copyOf(sslist);
        return UpcsmReportSender.of(mainline, snapshot);
    }

    @Override
    public UpcsmReportSender get() throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodGet[] params = new SimpleMethodGet[] {
            SimpleMethodGetParamsDefault.of()
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterGet get = SimpleStatusOuterGet.of(params, phaser);
        SimpleFutureGet future = holder.get(get);
        PgResult mainline = (PgResult)future.get().get(0);
        ImmutableList<PgResult> snapshot = ImmutableList.copyOf(sslist);
        return UpcsmReportSender.of(mainline, snapshot);
    }

    @Override
    public UpcsmReportSender del() throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodDel[] params = new SimpleMethodDel[] {
            SimpleMethodDelParamsDefault.of()
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterDel del = SimpleStatusOuterDel.of(params, phaser);
        SimpleFutureDel future = holder.del(del);
        PgResult mainline = (PgResult)future.get().get(0);
        ImmutableList<PgResult> snapshot = ImmutableList.copyOf(sslist);
        return UpcsmReportSender.of(mainline, snapshot);
    }

    @Override
    public UpcsmReportSender pst(LogSequenceNumber lsn) //
        throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodPst[] params = new SimpleMethodPst[] {
            MainlineMethodPstParamsRecvLsn.of(lsn)
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterPst pst = SimpleStatusOuterPst.of(params, phaser);
        SimpleFuturePst future = holder.pst(pst);
        PgResult mainline = (PgResult)future.get().get(0);
        ImmutableList<PgResult> snapshot = ImmutableList.copyOf(sslist);
        return UpcsmReportSender.of(mainline, snapshot);
    }

    @Override
    public UpcsmSender pst(SnapshotConfig config) //
        throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodPst[] params = new SimpleMethodPst[] {
            MainlineSnapshotPstParams.of(config)
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterPst pst = SimpleStatusOuterPst.of(params, phaser);
        SimpleFuturePst future = holder.pst(pst);
        PgResult mainline = (PgResult)future.get().get(0);
        if (!(mainline instanceof PgResultNormalSnapshot)) {
            return this;
        }
        TransferQueue<PgRecord> q = new LinkedTransferQueue<>();
        SimpleMethodPut[] put = new SimpleMethodPut[] {
            SimpleMethodPutParamsDefault.of()
        };
        SimpleStatusOuterPut s = SimpleStatusOuterPut.of(put, new Phaser(2));
        AtomicReference<SimpleStatus> status = new AtomicReference<>(s);
        Thread thread = new Thread(Snapshot.of(config, status, q));
        return UpcsmSenderSnapshotLockingRel.of(this, thread, q, status);
    }
}

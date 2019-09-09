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
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.*;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderResult;
import com.hktcode.pgstack.ruoshui.upper.pgsender.Snapshot;
import com.hktcode.pgstack.ruoshui.upper.pgsender.SnapshotConfig;
import org.postgresql.replication.LogSequenceNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpcsmThreadMainline extends UpcsmThread
{
    public final List<PgsenderResult<UpcsmFetchRecordSnapshot, SnapshotConfig>> sslist //
        = new ArrayList<>();

    public final TransferQueue<MainlineRecord> tqueue;

    public static UpcsmThreadMainline of(MainlineConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("thread");
        }
        TransferQueue<MainlineRecord> tqueue = new LinkedTransferQueue<>();
        SimpleMethodPut[] put = new SimpleMethodPut[] {
            SimpleMethodPutParamsDefault.of()
        };
        SimpleStatusOuterPut s = SimpleStatusOuterPut.of(put, new Phaser(2));
        AtomicReference<SimpleStatus> status = new AtomicReference<>(s);
        Thread thread = new Thread(Mainline.of(config, status, tqueue));
        return new UpcsmThreadMainline(thread, tqueue, status);
    }

    @Override
    public UpperConsumerRecord poll(long timeout, UpcsmActionRun action) //
        throws InterruptedException
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        MainlineRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            return record.toUpcsmRecord();
        }
        else if (this.thread.isAlive()) {
            return null;
        }
        else {
            // TODO: throw new DelegateNotAliveException();
            throw new RuntimeException();
        }
    }

    private UpcsmThreadMainline //
        /* */( Thread thread //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) //
    {
        super(thread, status);
        this.tqueue = tqueue;
    }

    @Override
    public UpcsmReportFetchThread put() throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleFuturePut future = holder.put();
        thread.start();
        PgsenderResult<MainlineRecord, MainlineConfig> mainline //
            = (PgsenderResult<MainlineRecord, MainlineConfig>)future.get().get(0);
        ImmutableList<PgsenderResult<UpcsmFetchRecordSnapshot, SnapshotConfig>> snapshot = ImmutableList.copyOf(sslist);
        return UpcsmReportFetchThread.of(mainline, snapshot);
    }

    @Override
    public UpcsmReportFetchThread get() throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodGet[] params = new SimpleMethodGet[] {
            SimpleMethodGetParamsDefault.of()
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterGet get = SimpleStatusOuterGet.of(params, phaser);
        SimpleFutureGet future = holder.get(get);
        PgsenderResult<MainlineRecord, MainlineConfig> mainline //
            = (PgsenderResult<MainlineRecord, MainlineConfig>)future.get().get(0);
        ImmutableList<PgsenderResult<UpcsmFetchRecordSnapshot, SnapshotConfig>> snapshot = ImmutableList.copyOf(sslist);
        return UpcsmReportFetchThread.of(mainline, snapshot);
    }

    @Override
    public UpcsmReportFetchThread del() throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodDel[] params = new SimpleMethodDel[] {
            SimpleMethodDelParamsDefault.of()
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterDel del = SimpleStatusOuterDel.of(params, phaser);
        SimpleFutureDel future = holder.del(del);
        PgsenderResult<MainlineRecord, MainlineConfig> mainline
            = (PgsenderResult<MainlineRecord, MainlineConfig>)future.get().get(0);
        ImmutableList<PgsenderResult<UpcsmFetchRecordSnapshot, SnapshotConfig>> snapshot
            = ImmutableList.copyOf(sslist);
        return UpcsmReportFetchThread.of(mainline, snapshot);
    }

    @Override
    public UpcsmReportFetchThread pst(LogSequenceNumber lsn) //
        throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodPst[] params = new SimpleMethodPst[] {
            MainlineMethodPstParamsRecvLsn.of(lsn)
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterPst pst = SimpleStatusOuterPst.of(params, phaser);
        SimpleFuturePst future = holder.pst(pst);
        PgsenderResult<MainlineRecord, MainlineConfig> mainline //
            = (PgsenderResult<MainlineRecord, MainlineConfig>)future.get().get(0);
        ImmutableList<PgsenderResult<UpcsmFetchRecordSnapshot, SnapshotConfig>> snapshot = ImmutableList.copyOf(sslist);
        return UpcsmReportFetchThread.of(mainline, snapshot);
    }

    @Override
    public UpcsmThread pst(SnapshotConfig config) //
        throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodPst[] params = new SimpleMethodPst[] {
            MainlineSnapshotPstParams.of(config)
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterPst pst = SimpleStatusOuterPst.of(params, phaser);
        SimpleFuturePst future = holder.pst(pst);
        PgsenderResult mainline = (PgsenderResult)future.get().get(0);
        if (!(mainline instanceof MainlineResultRunSnapshot)) {
            return this;
        }
        TransferQueue<UpcsmFetchRecordSnapshot> q = new LinkedTransferQueue<>();
        SimpleMethodPut[] put = new SimpleMethodPut[] {
            SimpleMethodPutParamsDefault.of()
        };
        SimpleStatusOuterPut s = SimpleStatusOuterPut.of(put, new Phaser(2));
        AtomicReference<SimpleStatus> status = new AtomicReference<>(s);
        Thread thread = new Thread(Snapshot.of(config, status, q));
        return UpcsmThreadSnapshotLockingRel.of(this, thread, q, status);
    }
}

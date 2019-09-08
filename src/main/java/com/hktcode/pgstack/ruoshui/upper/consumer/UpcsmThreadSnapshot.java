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
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterDel;
import com.hktcode.bgsimple.status.SimpleStatusOuterGet;
import com.hktcode.bgsimple.status.SimpleStatusOuterPst;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotResult;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.Phaser;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class UpcsmThreadSnapshot extends UpcsmThread
{
    protected final TransferQueue<UpcsmFetchRecordSnapshot> tqueue;

    protected UpcsmThreadSnapshot(UpcsmThreadSnapshot thread)
    {
        super(thread.mlxact, thread.thread, thread.status);
        this.tqueue = thread.tqueue;
    }

    protected UpcsmThreadSnapshot
        /* */( UpcsmThreadMainline mlxact
        /* */, Thread thread
        /* */, TransferQueue<UpcsmFetchRecordSnapshot> tqueue
        /* */, AtomicReference<SimpleStatus> status
        /* */)
    {
        super(mlxact, thread, status);
        this.tqueue = tqueue;
    }

    @Override
    public UpcsmReportFetchThread put() throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleFuturePut future = holder.put();
        thread.start();
        UpcsmReportFetchThread ml = this.mlxact.get();
        SnapshotResult snapshot = (SnapshotResult)future.get().get(0);
        ImmutableList<SnapshotResult> list = ImmutableList
            .<SnapshotResult>builder() //
            .addAll(ml.snapshot) //
            .add(snapshot) //
            .build();
        return UpcsmReportFetchThread.of(ml.mainline, list);
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
        UpcsmReportFetchThread ml = this.mlxact.get();
        SnapshotResult snapshot = (SnapshotResult)future.get().get(0);
        ImmutableList<SnapshotResult> list = ImmutableList
            .<SnapshotResult>builder() //
            .addAll(ml.snapshot) //
            .add(snapshot) //
            .build();
        return UpcsmReportFetchThread.of(ml.mainline, list);
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
        UpcsmReportFetchThread ml = this.mlxact.del();
        SnapshotResult snapshot = (SnapshotResult)future.get().get(0);
        ImmutableList<SnapshotResult> list = ImmutableList
            .<SnapshotResult>builder() //
            .addAll(ml.snapshot) //
            .add(snapshot) //
            .build();
        return UpcsmReportFetchThread.of(ml.mainline, list);
    }

    @Override
    public UpcsmReportFetchThread pst(LogSequenceNumber lsn) //
        throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodPst[] params = new SimpleMethodPst[] {
            UpperMethodPstParamsRecvLsn.of(lsn)
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterPst pst = SimpleStatusOuterPst.of(params, phaser);
        SimpleFuturePst future = holder.pst(pst);
        UpcsmReportFetchThread ml = this.mlxact.pst(lsn);
        SnapshotResult snapshot = (SnapshotResult)future.get().get(0);
        ImmutableList<SnapshotResult> list = ImmutableList
            .<SnapshotResult>builder() //
            .addAll(ml.snapshot) //
            .add(snapshot) //
            .build();
        return UpcsmReportFetchThread.of(ml.mainline, list);
    }
}

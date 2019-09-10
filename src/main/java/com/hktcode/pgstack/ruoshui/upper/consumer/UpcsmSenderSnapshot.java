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
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecord;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgResult;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.Phaser;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class UpcsmSenderSnapshot extends UpcsmSender
{
    public final UpcsmSenderMainline mlxact;

    protected final TransferQueue<PgRecord> tqueue;

    protected UpcsmSenderSnapshot(UpcsmSenderSnapshot thread)
    {
        super(thread.thread, thread.status);
        this.mlxact = thread.mlxact;
        this.tqueue = thread.tqueue;
    }

    protected UpcsmSenderSnapshot
        /* */( UpcsmSenderMainline mlxact
        /* */, Thread thread
        /* */, TransferQueue<PgRecord> tqueue
        /* */, AtomicReference<SimpleStatus> status
        /* */)
    {
        super(thread, status);
        this.mlxact = mlxact;
        this.tqueue = tqueue;
    }

    @Override
    public UpcsmReportSender put() throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleFuturePut future = holder.put();
        thread.start();
        UpcsmReportSender ml = this.mlxact.get();
        PgResult snapshot = (PgResult)future.get().get(0);
        ImmutableList<PgResult> list = ImmutableList
            .<PgResult>builder() //
            .addAll(ml.snapshot) //
            .add(snapshot) //
            .build();
        return UpcsmReportSender.of(ml.mainline, list);
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
        UpcsmReportSender ml = this.mlxact.get();
        PgResult snapshot = (PgResult)future.get().get(0);
        ImmutableList<PgResult> list = ImmutableList
            .<PgResult>builder() //
            .addAll(ml.snapshot) //
            .add(snapshot) //
            .build();
        return UpcsmReportSender.of(ml.mainline, list);
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
        UpcsmReportSender ml = this.mlxact.del();
        PgResult snapshot = (PgResult)future.get().get(0);
        ImmutableList<PgResult> list = ImmutableList
            .<PgResult>builder() //
            .addAll(ml.snapshot) //
            .add(snapshot) //
            .build();
        return UpcsmReportSender.of(ml.mainline, list);
    }

    @Override
    public UpcsmReportSender pst(LogSequenceNumber lsn) //
        throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodPst[] params = new SimpleMethodPst[] {
            UpperMethodPstParamsRecvLsn.of(lsn)
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterPst pst = SimpleStatusOuterPst.of(params, phaser);
        SimpleFuturePst future = holder.pst(pst);
        UpcsmReportSender ml = this.mlxact.pst(lsn);
        PgResult snapshot = (PgResult)future.get().get(0);
        ImmutableList<PgResult> list = ImmutableList
            .<PgResult>builder() //
            .addAll(ml.snapshot) //
            .add(snapshot) //
            .build();
        return UpcsmReportSender.of(ml.mainline, list);
    }
}

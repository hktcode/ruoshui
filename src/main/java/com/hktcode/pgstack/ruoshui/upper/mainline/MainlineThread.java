/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.future.SimpleFutureDel;
import com.hktcode.bgsimple.future.SimpleFuturePst;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperMethodPstParamsRecvLsn;
import com.hktcode.pgstack.ruoshui.upper.consumer.FetchThreadThrowsErrorException;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpperConsumerReportFetchThread;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpperConsumerThreadBasic;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotResult;
import org.postgresql.replication.LogSequenceNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineThread extends UpperConsumerThreadBasic
{
    private final List<SnapshotResult> sslist = new ArrayList<>();

    private final Thread thread;

    private final TransferQueue<MainlineRecord> tqueue;

    private final AtomicReference<SimpleStatus> status;

    public static MainlineThread of(MainlineConfig config) //
        throws InterruptedException
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
        thread.start();
        MainlineFuturePut future = MainlineFuturePut.of(status, s);
        future.get();
        return new MainlineThread(thread, tqueue, status);
    }

    @Override
    public UpperConsumerRecord poll(long timeout) throws InterruptedException
    {
        MainlineRecordNormal record //
            = (MainlineRecordNormal)this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        SimpleStatus origin;
        if (record != null) {
            return UpperConsumerRecord.of(record.lsn, record.msg);
        }
        else if (this.thread.isAlive()) {
            return null;
        }
        else if ((origin = this.status.get()) instanceof SimpleStatusInnerEnd) {
            throw new FetchThreadThrowsErrorException();
        }
        else {
            // TODO: throw new DelegateNotAliveException();
            throw new RuntimeException();
        }
    }

    private MainlineThread //
        /* */( Thread thread //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) //
    {
        this.thread = thread;
        this.tqueue = tqueue;
        this.status = status;
    }

    @Override
    public UpperConsumerReportFetchThread del() throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodDel[] params = new SimpleMethodDel[] {
            SimpleMethodDelParamsDefault.of()
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterDel del = SimpleStatusOuterDel.of(params, phaser);
        SimpleFutureDel future = holder.del(del);
        MainlineResult mainline = (MainlineResult)future.get().get(0);
        ImmutableList<SnapshotResult> snapshot = ImmutableList.copyOf(sslist);
        return UpperConsumerReportFetchThread.of(mainline, snapshot);
    }

    @Override
    public UpperConsumerReportFetchThread pst(LogSequenceNumber lsn) //
        throws InterruptedException
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodPst[] params = new SimpleMethodPst[] {
            UpperMethodPstParamsRecvLsn.of(lsn)
        };
        Phaser phaser = new Phaser(2);
        SimpleStatusOuterPst pst = SimpleStatusOuterPst.of(params, phaser);
        SimpleFuturePst future = holder.pst(pst);
        MainlineResult mainline = (MainlineResult)future.get().get(0);
        ImmutableList<SnapshotResult> snapshot = ImmutableList.copyOf(sslist);
        return UpperConsumerReportFetchThread.of(mainline, snapshot);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.method.SimpleMethodDelParams;
import com.hktcode.bgsimple.method.SimpleMethodDelParamsDefault;
import com.hktcode.bgsimple.method.SimpleMethodPutParams;
import com.hktcode.bgsimple.method.SimpleMethodPutParamsDefault;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterDel;
import com.hktcode.bgsimple.status.SimpleStatusOuterPut;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpperConsumerThreadBasic;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineThread extends UpperConsumerThreadBasic
{
    private final Thread thread;

    private final TransferQueue<MainlineRecord> tqueue;

    private final AtomicReference<SimpleStatus> status;

    public static MainlineThread of(MainlineConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("thread");
        }
        TransferQueue<MainlineRecord> tqueue = new LinkedTransferQueue<>();
        SimpleMethodPutParams[] put = new SimpleMethodPutParams[] {
            SimpleMethodPutParamsDefault.of()
        };
        SimpleStatus s = SimpleStatusOuterPut.of(put, new Phaser(2));
        AtomicReference<SimpleStatus> status = new AtomicReference<>(s);
        Thread thread = new Thread(Mainline.of(config, status, tqueue));
        thread.start();
        return new MainlineThread(thread, tqueue, status);
    }

    @Override
    public UpperConsumerRecord poll(long timeout) throws InterruptedException
    {
        MainlineRecordNormal record //
            = (MainlineRecordNormal)this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            return UpperConsumerRecord.of(record.lsn, record.msg);
        }
        else if (this.thread.isAlive()) {
            return null;
        }
        else {
            // TODO: throw new DelegateNotAliveException();
            throw new RuntimeException();
        }
    }

    protected MainlineThread //
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
    public String del()
    {
        SimpleHolder holder = SimpleHolder.of(status);
        SimpleMethodDelParams[] params = new SimpleMethodDelParams[] {
            SimpleMethodDelParamsDefault.of()
        };
        SimpleStatusOuterDel status = SimpleStatusOuterDel.of(params, new Phaser(2));
        SimpleStatusOuterDel del = holder.del(status);
        return null;
    }

    @Override
    public void pst(LogSequenceNumber lsn)
    {

    }
}

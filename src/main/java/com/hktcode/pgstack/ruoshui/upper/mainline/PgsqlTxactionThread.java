/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerThreadBasic;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PgsqlTxactionThread extends UpperConsumerThreadBasic<PgsqlTxactionRecord>
{
    public static PgsqlTxactionThread of
        /* */( MainlineConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("thread");
        }
        if (status == null) {
            throw new ArgumentNullException("tqueue");
        }
        TransferQueue<PgsqlTxactionRecord> tqueue = new LinkedTransferQueue<>();
        Thread thread = new Thread(PgsqlTxaction.of(config, tqueue, status));
        thread.start();
        return new PgsqlTxactionThread(thread, tqueue, status);
    }

    private final AtomicReference<SimpleStatus> status;

    @Override
    public UpperConsumerRecord //
    poll(long timeout, UpperConsumerMetric metric)
        throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        SimpleStatus s;
        PgsqlTxactionRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            return record.update(metric);
        }
        else if (this.thread.isAlive()) {
            return null;
        }
        else if ((s = this.status.get()) instanceof SimpleStatusInnerEnd) {
            // TODO:
            throw new RuntimeException(s.toString());
        }
        else {
            // TODO: throw new DelegateNotAliveException();
            throw new RuntimeException();
        }
    }

    protected PgsqlTxactionThread(Thread thread, TransferQueue<PgsqlTxactionRecord> tqueue, AtomicReference<SimpleStatus> status)
    {
        super(thread, tqueue);
        this.status = status;
    }
}

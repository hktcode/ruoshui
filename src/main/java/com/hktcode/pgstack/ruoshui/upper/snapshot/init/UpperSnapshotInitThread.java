/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.init;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerThreadBasic;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperSnapshotConfig;
import org.postgresql.jdbc.PgConnection;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpperSnapshotInitThread extends UpperConsumerThreadBasic<UpperSnapshotInitRecord>
{
    public static UpperSnapshotInitThread of
        /* */( UpperSnapshotConfig config
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status
        /* */, PgConnection pgrepl
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        TransferQueue<UpperSnapshotInitRecord> tqueue = new LinkedTransferQueue<>();
        UpperSnapshotInitSender s = UpperSnapshotInitSender.of(tqueue, status);
        Thread thread = new Thread(UpperSnapshotInit.of(config.iniSnapshot, s, pgrepl));
        thread.start();
        return new UpperSnapshotInitThread(thread, config, status, pgrepl, tqueue);
    }

    private UpperSnapshotInitThread
        /* */( Thread thread
        /* */, UpperSnapshotConfig config
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status
        /* */, PgConnection pgrepl
        /* */, TransferQueue<UpperSnapshotInitRecord> tqueue
        /* */)
    {
        super(thread, tqueue);
        this.status = status;
        this.pgrepl = pgrepl;
        this.config = config;
    }

    public final AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status;

    public final PgConnection pgrepl;

    public final UpperSnapshotConfig config;

    @Override
    public UpperConsumerRecord poll(long timeout, UpperConsumerMutableMetric metric) //
        throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        UpperSnapshotInitRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            return record.update(config, pgrepl, status, metric);
        }
        else if (!this.thread.isAlive()) {
            // TODO: throw new DelegateNotAliveException();
            throw new RuntimeException();
        }
        else {
            return null;
        }
    }
}

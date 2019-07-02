/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.post;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumer;
import com.hktcode.pgstack.ruoshui.upper.UpperJunction;
import com.hktcode.pgstack.ruoshui.upper.UpperProducer;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpperSnapshotPostThreadLockingRel extends UpperSnapshotPostThread
{
    public static UpperSnapshotPostThreadLockingRel of //
        /* */(PgSnapshotConfig config //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
        /* */, MainlineThread xact //
        /* */) //
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (xact == null) {
            throw new ArgumentNullException("xact");
        }
        TransferQueue<UpperSnapshotPostRecord> q = new LinkedTransferQueue<>();
        UpperSnapshotPostSender sender = UpperSnapshotPostSender.of(q, status);
        UpperSnapshotPost r = UpperSnapshotPost.of(config, sender);
        Thread t = new Thread(r);
        t.start();
        return new UpperSnapshotPostThreadLockingRel(t, xact, q);
    }

    private UpperSnapshotPostThreadLockingRel
        /* */( Thread thread
        /* */, MainlineThread xact
        /* */, TransferQueue<UpperSnapshotPostRecord> tqueue
        /* */)
    {
        super(thread, tqueue, xact);
    }

    private static final Logger logger = LoggerFactory.getLogger(UpperSnapshotPostThreadLockingRel.class);

    @Override
    public UpperConsumerRecord poll(long timeout, UpperConsumerMutableMetric metric) //
        throws InterruptedException
    {
        UpperSnapshotPostRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            if (!(record instanceof UpperSnapshotPostRecordPauseWorld)
                && !(record instanceof UpperSnapshotPostRecordExecThrows)) {
                throw new RuntimeException(); // TODO:
            }
            return record.update(metric, thread, tqueue, xact);
        }
        else if (this.thread.isAlive()) {
            return xact.poll(timeout, metric);
        }
        else {
            logger.error("snapshot post is not alive."); // TODO:
            metric.fetchThread = xact;
            return null;
        }
    }
}

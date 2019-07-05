/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotConfig;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThreadWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class SnapshotThreadLockingRel extends SnapshotThread
{
    public static SnapshotThreadLockingRel of //
        /* */(PgSnapshotConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, MainlineThreadWork xact //
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
        TransferQueue<SnapshotRecord> q = new LinkedTransferQueue<>();
        SnapshotSender sender = SnapshotSender.of(q, status);
        Snapshot r = Snapshot.of(config, sender);
        Thread t = new Thread(r);
        t.start();
        return new SnapshotThreadLockingRel(t, xact, q);
    }

    private SnapshotThreadLockingRel
        /* */( Thread thread
        /* */, MainlineThreadWork xact
        /* */, TransferQueue<SnapshotRecord> tqueue
        /* */)
    {
        super(thread, tqueue, xact);
    }

    private static final Logger logger = LoggerFactory.getLogger(SnapshotThreadLockingRel.class);

    @Override
    public UpperConsumerRecord poll(long timeout, UpperConsumerMetric metric) //
        throws InterruptedException
    {
        SnapshotRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            if (!(record instanceof SnapshotRecordPauseWorld)
                && !(record instanceof SnapshotRecordExecThrows)) {
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

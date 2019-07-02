/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThreadWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

public class SnapshotThreadCreateSlot extends SnapshotThread
{
    public static SnapshotThreadCreateSlot of //
        /* */(Thread thread //
        /* */, TransferQueue<SnapshotRecord> tqueue //
        /* */, MainlineThreadWork xact //
        /* */)
    {
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        if (xact == null) {
            throw new ArgumentNullException("xact");
        }
        return new SnapshotThreadCreateSlot(thread, tqueue, xact);
    }

    private SnapshotThreadCreateSlot //
        /* */(Thread thread //
        /* */, TransferQueue<SnapshotRecord> tqueue //
        /* */, MainlineThreadWork xact //
        /* */) //
    {
        super(thread, tqueue, xact);
    }

    private static final Logger logger = LoggerFactory.getLogger(SnapshotThreadCreateSlot.class);

    @Override
    public UpperConsumerRecord poll(long timeout, UpperConsumerMutableMetric metric) throws InterruptedException
    {
        SnapshotRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            if (!(record instanceof SnapshotRecordCreateSlot)
               && !(record instanceof SnapshotRecordExecThrows)) {
                throw new RuntimeException(); // TODO:
            }
            return record.update(metric, thread, tqueue, xact);
        }
        if (!thread.isAlive()) {
            logger.error("snapshot post is not alive."); // TODO:
            metric.fetchThread = xact;
        }
        return null;
    }
}

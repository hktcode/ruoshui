/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

public class UpperSnapshotPostThreadCreateSlot extends UpperSnapshotPostThread
{
    public static UpperSnapshotPostThreadCreateSlot of //
        /* */( Thread thread //
        /* */, TransferQueue<UpperSnapshotPostRecord> tqueue //
        /* */, MainlineThread xact //
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
        return new UpperSnapshotPostThreadCreateSlot(thread, tqueue, xact);
    }

    private UpperSnapshotPostThreadCreateSlot //
        /* */(Thread thread //
        /* */, TransferQueue<UpperSnapshotPostRecord> tqueue //
        /* */, MainlineThread xact //
        /* */) //
    {
        super(thread, tqueue, xact);
    }

    private static final Logger logger = LoggerFactory.getLogger(UpperSnapshotPostThreadCreateSlot.class);

    @Override
    public UpperConsumerRecord poll(long timeout, UpperConsumerMutableMetric metric) throws InterruptedException
    {
        UpperSnapshotPostRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (record != null) {
            if (!(record instanceof UpperSnapshotPostRecordCreateSlot)
               && !(record instanceof UpperSnapshotPostRecordExecThrows)) {
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

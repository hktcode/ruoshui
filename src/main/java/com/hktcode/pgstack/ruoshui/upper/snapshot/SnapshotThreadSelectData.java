/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThreadWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

public class SnapshotThreadSelectData extends SnapshotThread
{
    private static final Logger logger //
        = LoggerFactory.getLogger(SnapshotThreadSelectData.class);
    public static SnapshotThreadSelectData of
        /* */( UpperConsumerRecord record
        /* */, MainlineThreadWork xact
        /* */, Thread thread
        /* */, TransferQueue<SnapshotRecord> tqueue
        /* */)
    {
        if (record == null) {
            throw new ArgumentNullException("record");
        }
        if (xact == null) {
            throw new ArgumentNullException("xact");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        return new SnapshotThreadSelectData(record, xact, thread, tqueue);
    }

    private final UpperConsumerRecord record;

    private SnapshotThreadSelectData
        /* */( UpperConsumerRecord record
        /* */, MainlineThreadWork xact
        /* */, Thread thread
        /* */, TransferQueue<SnapshotRecord> tqueue
        /* */)
    {
        super(thread, tqueue, xact);
        this.record = record;
    }

    @Override
    public UpperConsumerRecord poll(long timeout, UpperConsumerMetric metric)
        throws InterruptedException
    {
        SnapshotRecord r = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (r != null) {
            if (!(r instanceof SnapshotRecordLogicalMsg)
                && !(r instanceof SnapshotRecordExecFinish)
                && !(r instanceof SnapshotRecordExecThrows)) {
                throw new RuntimeException(); // TODO:
            }
            UpperConsumerRecord result = r.update(metric, thread, tqueue, xact);
            return metric.fetchThread == xact ? this.record : result;
        }
        if (!thread.isAlive()) {
            logger.error("snapshot post is not alive."); // TODO:
            // metric.fetchThread = xact;
        }
        return null;
    }
}

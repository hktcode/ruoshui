/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot.post;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.txaction.UpperTxactionThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

public class UpperSnapshotPostThreadSelectData extends UpperSnapshotPostThread
{
    private static final Logger logger //
        = LoggerFactory.getLogger(UpperSnapshotPostThreadSelectData.class);
    public static UpperSnapshotPostThreadSelectData of
        /* */( UpperConsumerRecord record
        /* */, UpperTxactionThread xact
        /* */, Thread thread
        /* */, TransferQueue<UpperSnapshotPostRecord> tqueue
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
        return new UpperSnapshotPostThreadSelectData(record, xact, thread, tqueue);
    }

    private final UpperConsumerRecord record;

    private UpperSnapshotPostThreadSelectData
        /* */( UpperConsumerRecord record
        /* */, UpperTxactionThread xact
        /* */, Thread thread
        /* */, TransferQueue<UpperSnapshotPostRecord> tqueue
        /* */)
    {
        super(thread, tqueue, xact);
        this.record = record;
    }

    @Override
    public UpperConsumerRecord poll(long timeout, UpperConsumerMutableMetric metric)
        throws InterruptedException
    {
        UpperSnapshotPostRecord r = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (r != null) {
            if (!(r instanceof UpperSnapshotPostRecordLogicalMsg)
                && !(r instanceof UpperSnapshotPostRecordExecFinish)
                && !(r instanceof UpperSnapshotPostRecordExecThrows)) {
                throw new RuntimeException(); // TODO:
            }
            UpperConsumerRecord result = r.update(metric, thread, tqueue, xact);
            return metric.fetchThread == xact ? this.record : result;
        }
        if (!thread.isAlive()) {
            logger.error("snapshot post is not alive."); // TODO:
            metric.fetchThread = xact;
        }
        return null;
    }
}

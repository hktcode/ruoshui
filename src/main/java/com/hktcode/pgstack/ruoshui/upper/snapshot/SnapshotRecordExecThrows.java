/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThreadWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TransferQueue;

public class SnapshotRecordExecThrows implements SnapshotRecord
{
    public static SnapshotRecordExecThrows of(Throwable throwable)
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        return new SnapshotRecordExecThrows(throwable);
    }

    public final Throwable throwable;

    private static final Logger logger = LoggerFactory.getLogger(SnapshotRecordExecThrows.class);
    @Override
    public UpperConsumerRecord update(UpperConsumerMutableMetric metric, Thread thread, TransferQueue<SnapshotRecord> tqueue, MainlineThreadWork xact)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        if (xact == null) {
            throw new ArgumentNullException("xact");
        }
        logger.error("throws exception: ", throwable);
        metric.fetchThread = xact;
        return null;
    }

    private SnapshotRecordExecThrows(Throwable throwable)
    {
        this.throwable = throwable;
    }
}

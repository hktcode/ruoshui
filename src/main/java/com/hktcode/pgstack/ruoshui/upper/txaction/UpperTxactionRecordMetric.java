/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.txaction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

import java.util.concurrent.TransferQueue;

public class UpperTxactionRecordMetric implements UpperTxactionRecord
{
    public static UpperTxactionRecordMetric of //
        /* */( Thread thread //
        /* */, UpperTxactionMetric metric //
        /* */, TransferQueue<UpperTxactionRecord> tqueue //
        /* */) //
    {
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        return new UpperTxactionRecordMetric(thread, metric, tqueue);
    }

    private final Thread thread;

    private final UpperTxactionMetric metric;

    private final TransferQueue<UpperTxactionRecord> tqueue;

    @Override
    public UpperConsumerRecord update(UpperConsumerMutableMetric metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        metric.txactMetric = this.metric;
        metric.fetchThread = UpperTxactionThreadWork.of(thread, this.metric, tqueue);
        return null;
    }

    private UpperTxactionRecordMetric //
        /* */( Thread thread //
        /* */, UpperTxactionMetric metric //
        /* */, TransferQueue<UpperTxactionRecord> tqueue //
        /* */) //
    {
        this.thread = thread;
        this.metric = metric;
        this.tqueue = tqueue;
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

import java.util.concurrent.TransferQueue;

class MainlineRecordMetric implements MainlineRecord
{
    static MainlineRecordMetric of //
        /* */( Thread thread //
        /* */, MainlineMetricTxaction metric //
        /* */, TransferQueue<MainlineRecord> tqueue //
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
        return new MainlineRecordMetric(thread, metric, tqueue);
    }

    private final Thread thread;

    private final MainlineMetricTxaction metric;

    private final TransferQueue<MainlineRecord> tqueue;

    @Override
    public UpperConsumerRecord update(UpperConsumerMetric metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        // metric.txactMetric = this.metric; TODO:
        metric.fetchThread = MainlineThreadWork.of(thread, this.metric, tqueue);
        return null;
    }

    private MainlineRecordMetric //
        /* */( Thread thread //
        /* */, MainlineMetricTxaction metric //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */) //
    {
        this.thread = thread;
        this.metric = metric;
        this.tqueue = tqueue;
    }
}

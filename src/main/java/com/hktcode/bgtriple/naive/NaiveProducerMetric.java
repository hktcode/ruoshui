/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.naive;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class NaiveProducerMetric
{
    public static NaiveProducerMetric of(NaiveProducerMutableMetric metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new NaiveProducerMetric(metric);
    }

    public final ZonedDateTime createTime;

    public final long recordSize;

    public final long pollCounts;

    public final long pollMillis;

    public final long pushCounts;

    public final long pushMillis;

    protected NaiveProducerMetric(NaiveProducerMutableMetric metric)
    {
        this.createTime = metric.startMillis;
        this.recordSize = metric.recordCount;
        this.pollCounts = metric.fetchCounts;
        this.pollMillis = metric.fetchMillis;
        this.pushCounts = metric.offerCounts;
        this.pushMillis = metric.offerMillis;
    }
}

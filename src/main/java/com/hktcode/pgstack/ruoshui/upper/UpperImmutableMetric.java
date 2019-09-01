/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.time.ZonedDateTime;

public class UpperImmutableMetric
{
    public static UpperImmutableMetric of(UpperMetric metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpperImmutableMetric(metric);
    }

    public final ZonedDateTime start;

    public final long sleepedCount;

    public final long sleepedMillis;

    public final long commitedLsnCount;

    public final LogSequenceNumber lastReceiveLsn;

    private UpperImmutableMetric(UpperMetric metric)
    {
        this.start = metric.createTime;
        this.sleepedCount = metric.sleepedCount;
        this.sleepedMillis = metric.sleepedMillis;
        this.commitedLsnCount = metric.commitedLsnCount;
        this.lastReceiveLsn = LogSequenceNumber.valueOf(metric.lastReceiveLsn);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;

public class UpperConsumerMetricErr
{
    public static UpperConsumerMetricErr of //
        (UpperConsumerActionRun action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpperConsumerMetricErr(action, throwsError);
    }

    public static UpperConsumerMetricErr of //
        (UpperConsumerMetricEnd metric, Throwable throwsError)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpperConsumerMetricErr(metric, throwsError);
    }

    public final long actionStart;

    public final long recordCount;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long totalMillis;

    public final String statusInfor;

    // TODO: public final UpperConsumerReportFetchThread fetchThread;

    public final Throwable throwsError;

    private UpperConsumerMetricErr //
    (UpperConsumerActionRun action, Throwable throwsError)
    {
        long finish = System.currentTimeMillis();
        this.actionStart = action.actionStart;
        this.recordCount = action.recordCount;
        this.fetchCounts = action.fetchCounts;
        this.fetchMillis = action.fetchMillis;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.totalMillis = finish - action.actionStart;
        this.statusInfor = action.statusInfor;
        this.throwsError = throwsError;
    }

    private UpperConsumerMetricErr //
    (UpperConsumerMetricEnd metric, Throwable throwsError)
    {
        long finish = System.currentTimeMillis();
        this.actionStart = metric.actionStart;
        this.recordCount = metric.recordCount;
        this.fetchCounts = metric.fetchCounts;
        this.fetchMillis = metric.fetchMillis;
        this.offerCounts = metric.offerCounts;
        this.offerMillis = metric.offerMillis;
        this.totalMillis = finish - metric.actionStart;
        this.statusInfor = metric.statusInfor;
        this.throwsError = throwsError;
    }
}

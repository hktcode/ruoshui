/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmMetricErr
{
    public static UpcsmMetricErr of //
        (UpcsmActionRun action, UpcsmReportSender fetchThread, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (fetchThread == null) {
            throw new ArgumentNullException("fetchThread");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpcsmMetricErr(action, fetchThread, throwsError);
    }

    public static UpcsmMetricErr of //
        (UpcsmMetricEnd metric, Throwable throwsError)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpcsmMetricErr(metric, throwsError);
    }

    public final long actionStart;

    public final long recordCount;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long totalMillis;

    public final String statusInfor;

    public final UpcsmReportSender fetchThread;

    public final Throwable throwsError;

    private UpcsmMetricErr //
    (UpcsmActionRun action, UpcsmReportSender fetchThread, Throwable throwsError)
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
        this.fetchThread = fetchThread;
    }

    private UpcsmMetricErr //
    (UpcsmMetricEnd metric, Throwable throwsError)
    {
        long finish = System.currentTimeMillis();
        this.actionStart = metric.actionStart;
        this.recordCount = metric.recordCount;
        this.fetchCounts = metric.fetchCounts;
        this.fetchMillis = metric.fetchMillis;
        this.offerCounts = metric.offerCounts;
        this.offerMillis = metric.offerMillis;
        this.fetchThread = metric.fetchThread;
        this.statusInfor = metric.statusInfor;
        this.totalMillis = finish - metric.actionStart;
        this.throwsError = throwsError;
    }
}

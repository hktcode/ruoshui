/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;

public class UpperConsumerMetricEnd
{
    public static UpperConsumerMetricEnd of //
        (UpperConsumerActionRun action, UpperConsumerReportFetchThread fetchThread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (fetchThread == null) {
            throw new ArgumentNullException("fetchThread");
        }
        return new UpperConsumerMetricEnd(action, fetchThread);
    }

    public final long actionStart;

    public final long recordCount;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long logDatetime;

    public final long totalMillis;

    public final UpperConsumerReportFetchThread fetchThread;

    public final String statusInfor;

    private UpperConsumerMetricEnd //
    (UpperConsumerActionRun action //
        , UpperConsumerReportFetchThread fetchThread)
    {
        long finish = System.currentTimeMillis();
        this.actionStart = action.actionStart;
        this.recordCount = action.recordCount;
        this.fetchCounts = action.fetchCounts;
        this.fetchMillis = action.fetchMillis;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.logDatetime = action.logDatetime;
        this.statusInfor = action.statusInfor;
        this.totalMillis = finish - action.actionStart;
        this.fetchThread = fetchThread;
    }
}

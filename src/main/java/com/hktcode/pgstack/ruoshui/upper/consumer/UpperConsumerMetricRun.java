/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;

public class UpperConsumerMetricRun
{
    public static UpperConsumerMetricRun of(UpperConsumerActionRun action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new UpperConsumerMetricRun(action);
    }

    public final long actionStart;

    public final long recordCount;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    // TODO: public final UpperConsumerReportFetchThread fetchThread;

    public final String statusInfor;

    private UpperConsumerMetricRun(UpperConsumerActionRun action)
    {
        this.actionStart = action.actionStart;
        this.recordCount = action.recordCount;
        this.fetchCounts = action.fetchCounts;
        this.fetchMillis = action.fetchMillis;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.statusInfor = action.statusInfor;
    }
}

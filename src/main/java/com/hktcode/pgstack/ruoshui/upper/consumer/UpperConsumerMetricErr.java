/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;

public class UpperConsumerMetricErr
{
    public static UpperConsumerMetricErr of //
        (UpperConsumerActionRun action, String statusInfor, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (statusInfor == null) {
            throw new ArgumentNullException("statusInfor");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpperConsumerMetricErr(action, statusInfor, throwsError);
    }

    public final long actionStart;

    public final long recordCount;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long logDatetime;

    public final long totalMillis;

    public final String statusInfor;

    public final Throwable throwsError;

    private UpperConsumerMetricErr //
    (UpperConsumerActionRun action, String statusInfor, Throwable throwsError)
    {
        long finish = System.currentTimeMillis();
        this.actionStart = action.actionStart;
        this.recordCount = action.recordCount;
        this.fetchCounts = action.fetchCounts;
        this.fetchMillis = action.fetchMillis;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.logDatetime = action.logDatetime;
        this.totalMillis = finish - action.actionStart;
        this.statusInfor = statusInfor;
        this.throwsError = throwsError;
    }
}

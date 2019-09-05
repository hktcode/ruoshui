/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.junction;

import com.hktcode.lang.exception.ArgumentNullException;

public class UpjctMetricErr
{
    public static UpjctMetricErr of(UpjctActionRun action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpjctMetricErr(action, throwsError);
    }

    public static UpjctMetricErr of(UpjctActionEnd action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpjctMetricErr(action, throwsError);
    }

    public final long actionStart;

    public final long recordCount;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long totalMillis;

    public final long curLsnofcmt;

    public final long curSequence;

    public final String statusInfor;

    public final Throwable throwsError;

    private UpjctMetricErr(UpjctActionRun action, Throwable throwsError)
    {
        long finish = System.currentTimeMillis();
        this.actionStart = action.actionStart;
        this.recordCount = action.recordCount;
        this.fetchCounts = action.fetchCounts;
        this.fetchMillis = action.fetchMillis;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.curLsnofcmt = action.curLsnofcmt;
        this.curSequence = action.curSequence;
        this.statusInfor = action.statusInfor;
        this.totalMillis = finish - action.actionStart;
        this.throwsError = throwsError;
    }

    private UpjctMetricErr(UpjctActionEnd action, Throwable throwsError)
    {
        long finish = System.currentTimeMillis();
        this.actionStart = action.metric.actionStart;
        this.recordCount = action.metric.recordCount;
        this.fetchCounts = action.metric.fetchCounts;
        this.fetchMillis = action.metric.fetchMillis;
        this.offerCounts = action.metric.offerCounts;
        this.offerMillis = action.metric.offerMillis;
        this.curLsnofcmt = action.metric.curLsnofcmt;
        this.curSequence = action.metric.curSequence;
        this.statusInfor = action.metric.statusInfor;
        this.totalMillis = finish - action.metric.actionStart;
        this.throwsError = throwsError;
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperMetric;

public class UppdcMetricErr
{
    public static UppdcMetricErr of(UppdcActionRun action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UppdcMetricErr(action, throwsError);
    }

    public static UppdcMetricErr of(UppdcActionEnd action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UppdcMetricErr(action, throwsError);
    }

    public final long actionStart;

    public final long recordCount;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    /**
     * 描述当前状态的信息.
     */
    public final String statusInfor;

    public final long totalMillis;

    public final Throwable throwsError;

    private UppdcMetricErr(UppdcActionRun action, Throwable throwsError)
    {
        long finish = System.currentTimeMillis();
        this.actionStart = action.actionStart;
        this.recordCount = action.recordCount;
        this.fetchCounts = action.fetchCounts;
        this.fetchMillis = action.fetchMillis;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.statusInfor = action.statusInfor;
        this.totalMillis = finish - action.actionStart;
        this.throwsError = throwsError;
    }

    private UppdcMetricErr(UppdcActionEnd action, Throwable throwsError)
    {
        long finish = System.currentTimeMillis();
        this.actionStart = action.metric.actionStart;
        this.recordCount = action.metric.recordCount;
        this.fetchCounts = action.metric.fetchCounts;
        this.fetchMillis = action.metric.fetchMillis;
        this.offerCounts = action.metric.offerCounts;
        this.offerMillis = action.metric.offerMillis;
        this.statusInfor = action.metric.statusInfor;
        this.totalMillis = finish - action.metric.actionStart;
        this.throwsError = throwsError;
    }
}

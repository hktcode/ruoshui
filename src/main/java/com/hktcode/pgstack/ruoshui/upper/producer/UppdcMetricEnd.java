/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;

public class UppdcMetricEnd
{
    public static UppdcMetricEnd of(UppdcActionRun action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new UppdcMetricEnd(action);
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

    private UppdcMetricEnd(UppdcActionRun action)
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
    }
}

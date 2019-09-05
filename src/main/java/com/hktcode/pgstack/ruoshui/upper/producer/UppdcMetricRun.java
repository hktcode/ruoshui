/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class UppdcMetricRun
{
    public static UppdcMetricRun of(UppdcActionRun action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new UppdcMetricRun(action);
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

    private UppdcMetricRun(UppdcActionRun action)
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

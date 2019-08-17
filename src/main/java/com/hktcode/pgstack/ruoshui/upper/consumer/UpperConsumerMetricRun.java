/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;

public class UpperConsumerMetricRun
{
    public static UpperConsumerMetricRun of //
        (UpperConsumerActionRun action, String statusInfor)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (statusInfor == null) {
            throw new ArgumentNullException("statusInfor");
        }
        return new UpperConsumerMetricRun(action, statusInfor);
    }

    public final long actionStart;

    public final long recordCount;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final String statusInfor;

    private UpperConsumerMetricRun //
        (UpperConsumerActionRun action, String statusInfor)
    {
        this.actionStart = action.actionStart;
        this.recordCount = action.recordCount;
        this.fetchCounts = action.fetchCounts;
        this.fetchMillis = action.fetchMillis;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.statusInfor = statusInfor;
    }
}

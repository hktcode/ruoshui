/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmMetricRun
{
    public static UpcsmMetricRun of(UpcsmActionRun action, UpcsmReportFetchThread fetchThread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (fetchThread == null) {
            throw new ArgumentNullException("fetchThread");
        }
        return new UpcsmMetricRun(action, fetchThread);
    }

    public final long actionStart;

    public final long recordCount;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final UpcsmReportFetchThread fetchThread;

    public final String statusInfor;

    private UpcsmMetricRun(UpcsmActionRun action, UpcsmReportFetchThread fetchThread)
    {
        this.actionStart = action.actionStart;
        this.recordCount = action.recordCount;
        this.fetchCounts = action.fetchCounts;
        this.fetchMillis = action.fetchMillis;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.statusInfor = action.statusInfor;
        this.fetchThread = fetchThread;
    }
}

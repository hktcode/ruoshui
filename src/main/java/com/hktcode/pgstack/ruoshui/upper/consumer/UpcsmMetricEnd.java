/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmMetricEnd
{
    public static UpcsmMetricEnd of //
    (UpcsmActionRun action, UpcsmReportSender fetchThread) //
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (fetchThread == null) {
            throw new ArgumentNullException("fetchThread");
        }
        return new UpcsmMetricEnd(action, fetchThread);
    }

    public final long actionStart;

    public final long recordCount;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long totalMillis;

    public final UpcsmReportSender fetchThread;

    public final String statusInfor;

    private UpcsmMetricEnd //
    (UpcsmActionRun action, UpcsmReportSender fetchThread) //
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
        this.fetchThread = fetchThread;
    }
}

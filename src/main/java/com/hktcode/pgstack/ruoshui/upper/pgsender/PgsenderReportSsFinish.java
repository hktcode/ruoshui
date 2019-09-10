/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderReportSsFinish
{
    public static PgsenderReportSsFinish of(PgActionDataSsFinish action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportSsFinish(action, finish);
    }

    public final long totalMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long recordCount;

    private PgsenderReportSsFinish(PgActionDataSsFinish action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.recordCount = action.recordCount;
    }
}

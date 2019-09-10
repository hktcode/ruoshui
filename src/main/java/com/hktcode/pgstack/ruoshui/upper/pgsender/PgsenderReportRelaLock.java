/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderReportRelaLock
{
    static PgsenderReportRelaLock of(PgActionDataRelaLock action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportRelaLock(action, finish);
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    private PgsenderReportRelaLock(PgActionDataRelaLock action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
    }
}

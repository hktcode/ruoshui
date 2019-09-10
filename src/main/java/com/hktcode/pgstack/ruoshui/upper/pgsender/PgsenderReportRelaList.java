/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderReportRelaList
{
    public static PgsenderReportRelaList of(PgActionDataRelaList action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportRelaList(action, finish);
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    public final long relationLst;

    public final ImmutableList<String> retryReason;

    private PgsenderReportRelaList(PgActionDataRelaList action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
        this.retryReason = action.retryReason;
        this.relationLst = action.relationLst.size();
    }
}

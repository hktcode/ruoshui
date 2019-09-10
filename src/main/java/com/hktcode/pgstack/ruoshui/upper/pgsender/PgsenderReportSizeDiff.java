/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderReportSizeDiff
{
    static <C extends PgsenderConfig>  //
    PgsenderReportSizeDiff of(PgsenderActionDataSizeDiff<C> action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportSizeDiff(action, finish);
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    public final long newRelalist;

    private <C extends PgsenderConfig>
    PgsenderReportSizeDiff(PgsenderActionDataSizeDiff<C> action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
        this.newRelalist = action.rsnextCount;
    }
}

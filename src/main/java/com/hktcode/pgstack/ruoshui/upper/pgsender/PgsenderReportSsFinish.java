/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderReportSsFinish
{
    public static <R, C extends PgsenderConfig<R, C>> PgsenderReportSsFinish //
    of(PgsenderActionDataSsFinish<R, C> action, long finish)
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

    private <R, C extends PgsenderConfig<R, C>>
    PgsenderReportSsFinish(PgsenderActionDataSsFinish<R, C> action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.recordCount = action.recordCount;
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderReportSsBegins
{
    static <C extends PgsenderConfig>
    PgsenderReportSsBegins of(PgsenderActionDataSsBegins<C> action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportSsBegins(action, finish);
    }

    public final long totalMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long recordCount;

    private <C extends PgsenderConfig>
    PgsenderReportSsBegins(PgsenderActionDataSsBegins<C> action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.recordCount = action.recordCount;
    }
}

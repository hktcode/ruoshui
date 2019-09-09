/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class PgsenderMetricEndTypelist implements PgsenderMetricEnd
{
    protected PgsenderMetricEndTypelist(PgsenderActionDataTypelist action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = PgsenderReportTypelist.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    public final PgsenderReportTypelist typelist;

    public final PgsenderReportComplete complete;
}

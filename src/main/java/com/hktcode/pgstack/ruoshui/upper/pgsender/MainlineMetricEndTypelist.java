/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class MainlineMetricEndTypelist implements PgsenderMetricEnd
{
    protected MainlineMetricEndTypelist(MainlineActionDataTypelist action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = MainlineReportTypelist.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    public final MainlineReportTypelist typelist;

    public final PgsenderReportComplete complete;

    @Override
    public abstract MainlineMetricErrTypelist toErrMetrics(Throwable throwerr);
}

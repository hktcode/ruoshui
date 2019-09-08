/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

public abstract class MainlineMetricEndTypelist implements MainlineMetricEnd
{
    protected MainlineMetricEndTypelist(MainlineActionDataTypelist action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = MainlineReportTypelist.of(action, finish);
        this.complete = MainlineReportComplete.of(finish);
    }

    public final MainlineReportTypelist typelist;

    public final MainlineReportComplete complete;

    @Override
    public abstract MainlineMetricErrTypelist toErrMetrics(Throwable throwerr);
}

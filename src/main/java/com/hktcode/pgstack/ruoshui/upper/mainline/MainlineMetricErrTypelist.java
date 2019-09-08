/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

public abstract class MainlineMetricErrTypelist implements MainlineMetricErr
{
    protected MainlineMetricErrTypelist(MainlineMetricEndTypelist metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.typelist = metric.typelist;
        this.throwerr = MainlineReportThrowErr.of(finish, throwerr);
    }

    public final MainlineReportTypelist typelist;

    public final MainlineReportThrowErr throwerr;
}

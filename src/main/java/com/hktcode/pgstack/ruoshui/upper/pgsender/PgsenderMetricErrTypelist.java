/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class PgsenderMetricErrTypelist implements PgsenderMetricErr
{
    protected PgsenderMetricErrTypelist(PgsenderMetricEndTypelist metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.typelist = metric.typelist;
        this.throwerr = PgsenderReportThrowErr.of(finish, throwerr);
    }

    public final PgsenderReportTypelist typelist;

    public final PgsenderReportThrowErr throwerr;
}

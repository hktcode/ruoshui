/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class PgMetricErrTypelist implements PgMetricErr
{
    protected PgMetricErrTypelist(PgMetricEndTypelist metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.typelist = metric.typelist;
        this.throwerr = PgReportThrowErr.of(finish, throwerr);
    }

    public final PgReportTypelist typelist;

    public final PgReportThrowErr throwerr;
}

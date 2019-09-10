/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class PgMetricErrTxaction implements PgMetricErr
{
    protected PgMetricErrTxaction(PgMetricEndTxaction action, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.typelist = action.typelist;
        this.txaction = action.txaction;
        this.throwerr = PgReportThrowErr.of(finish, throwerr);
    }

    public final PgReportTypelist typelist;

    public final PgReportTxaction txaction;

    public final PgReportThrowErr throwerr;
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

public abstract class MainlineMetricErrTxaction implements MainlineMetricErr
{
    protected MainlineMetricErrTxaction(MainlineMetricEndTxaction action, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.typelist = action.typelist;
        this.txaction = action.txaction;
        this.throwerr = MainlineReportThrowErr.of(finish, throwerr);
    }

    public final MainlineReportTypelist typelist;

    public final MainlineReportTxaction txaction;

    public final MainlineReportThrowErr throwerr;
}

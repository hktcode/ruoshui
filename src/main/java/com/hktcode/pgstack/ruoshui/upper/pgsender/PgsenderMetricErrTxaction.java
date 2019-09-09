/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class PgsenderMetricErrTxaction implements PgsenderMetricErr
{
    protected PgsenderMetricErrTxaction(PgsenderMetricEndTxaction action, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.typelist = action.typelist;
        this.txaction = action.txaction;
        this.throwerr = PgsenderReportThrowErr.of(finish, throwerr);
    }

    public final PgsenderReportTypelist typelist;

    public final PgsenderReportTxaction txaction;

    public final PgsenderReportThrowErr throwerr;
}

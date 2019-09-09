/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderMetricErr;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderReportThrowErr;

public abstract class MainlineMetricErrTxaction implements PgsenderMetricErr
{
    protected MainlineMetricErrTxaction(MainlineMetricEndTxaction action, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.typelist = action.typelist;
        this.txaction = action.txaction;
        this.throwerr = PgsenderReportThrowErr.of(finish, throwerr);
    }

    public final MainlineReportTypelist typelist;

    public final MainlineReportTxaction txaction;

    public final PgsenderReportThrowErr throwerr;
}

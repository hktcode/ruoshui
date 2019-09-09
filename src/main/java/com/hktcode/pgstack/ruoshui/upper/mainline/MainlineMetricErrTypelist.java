/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderMetricErr;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderReportThrowErr;

public abstract class MainlineMetricErrTypelist implements PgsenderMetricErr
{
    protected MainlineMetricErrTypelist(MainlineMetricEndTypelist metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.typelist = metric.typelist;
        this.throwerr = PgsenderReportThrowErr.of(finish, throwerr);
    }

    public final MainlineReportTypelist typelist;

    public final PgsenderReportThrowErr throwerr;
}

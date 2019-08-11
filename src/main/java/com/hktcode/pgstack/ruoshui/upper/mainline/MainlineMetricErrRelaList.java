/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrRelaList implements MainlineMetricErr
{
    static MainlineMetricErrRelaList //
    of(MainlineMetricEndRelaList metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new MainlineMetricErrRelaList(metric, throwerr);
    }

    private MainlineMetricErrRelaList(MainlineMetricEndRelaList metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.begin1st = metric.begin1st;
        this.relalist = metric.relalist;
        this.throwerr = MainlineReportThrowErr.of(finish, throwerr);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    public final MainlineReportThrowErr throwerr;
}

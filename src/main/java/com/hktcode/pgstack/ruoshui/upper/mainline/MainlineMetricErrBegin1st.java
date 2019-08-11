/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrBegin1st implements MainlineMetricErr
{
    public static MainlineMetricErrBegin1st //
    of(MainlineMetricEndBegin1st metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new MainlineMetricErrBegin1st(metric, throwerr);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportThrowErr throwerr;

    private MainlineMetricErrBegin1st(MainlineMetricEndBegin1st metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.begin1st = metric.begin1st;
        this.throwerr = MainlineReportThrowErr.of(finish, throwerr);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricErrRelaList implements PgMetricErr
{
    static PgMetricErrRelaList of(PgMetricEndRelaList metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new PgMetricErrRelaList(metric, throwerr);
    }

    private PgMetricErrRelaList(PgMetricEndRelaList metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.relalist = metric.relalist;
        this.throwerr = PgReportThrowErr.of(finish, throwerr);
    }

    public final PgReportRelaList relalist;

    public final PgReportThrowErr throwerr;
}

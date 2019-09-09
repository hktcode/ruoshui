/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricErrRelaLock implements PgsenderMetricErr
{
    static PgsenderMetricErrRelaLock of(PgsenderMetricEndRelaLock metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new PgsenderMetricErrRelaLock(metric, throwerr);
    }

    private PgsenderMetricErrRelaLock(PgsenderMetricEndRelaLock metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.relalist = metric.relalist;
        this.relalock = metric.relalock;
        this.throwerr = PgsenderReportThrowErr.of(finish, throwerr);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportThrowErr throwerr;
}

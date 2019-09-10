/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricEndRelaLock implements PgsenderMetricEnd
{
    static PgsenderMetricEndRelaLock of(PgActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricEndRelaLock(action);
    }

    private PgsenderMetricEndRelaLock(PgActionDataRelaLock action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = PgsenderReportRelaLock.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportComplete complete;

    @Override
    public PgsenderMetricErrRelaLock toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgsenderMetricErrRelaLock.of(this, throwerr);
    }
}

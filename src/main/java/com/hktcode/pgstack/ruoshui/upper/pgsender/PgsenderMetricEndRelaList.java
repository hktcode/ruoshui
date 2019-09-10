/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricEndRelaList implements PgsenderMetricEnd
{
    static PgsenderMetricEndRelaList of(PgActionDataRelaList action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricEndRelaList(action);
    }

    private PgsenderMetricEndRelaList(PgActionDataRelaList action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = PgsenderReportRelaList.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportComplete complete;

    @Override
    public PgsenderMetricErrRelaList toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgsenderMetricErrRelaList.of(this, throwerr);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricEndRelaList implements PgMetricEnd
{
    static PgMetricEndRelaList of(PgActionDataRelaList action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricEndRelaList(action);
    }

    private PgMetricEndRelaList(PgActionDataRelaList action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = PgReportRelaList.of(action, finish);
        this.complete = PgReportComplete.of(finish);
    }

    public final PgReportRelaList relalist;

    public final PgReportComplete complete;

    @Override
    public PgMetricErrRelaList toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgMetricErrRelaList.of(this, throwerr);
    }
}

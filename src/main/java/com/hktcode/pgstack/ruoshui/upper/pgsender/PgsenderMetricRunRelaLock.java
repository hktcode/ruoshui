/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricRunRelaLock implements PgsenderMetricRun
{
    static PgsenderMetricRunRelaLock of(PgActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunRelaLock(action);
    }

    private PgsenderMetricRunRelaLock(PgActionDataRelaLock action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = PgsenderReportRelaLock.of(action, finish);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;
}

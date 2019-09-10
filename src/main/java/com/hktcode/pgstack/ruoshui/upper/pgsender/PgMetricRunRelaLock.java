/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricRunRelaLock implements PgMetricRun
{
    static PgMetricRunRelaLock of(PgActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricRunRelaLock(action);
    }

    private PgMetricRunRelaLock(PgActionDataRelaLock action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = PgReportRelaLock.of(action, finish);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relalock;
}

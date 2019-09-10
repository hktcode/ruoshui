/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricRunRelaList implements PgMetricRun
{
    static PgMetricRunRelaList of(PgActionDataRelaList action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricRunRelaList(action);
    }

    private PgMetricRunRelaList(PgActionDataRelaList action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = PgReportRelaList.of(action, finish);
    }

    public final PgReportRelaList relalist;
}

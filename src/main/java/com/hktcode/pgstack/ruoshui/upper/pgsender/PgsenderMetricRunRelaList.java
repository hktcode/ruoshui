/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricRunRelaList implements PgsenderMetricRun
{
    static <R, C extends PgsenderConfig> //
    PgsenderMetricRunRelaList of(PgsenderActionDataRelaList<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunRelaList(action);
    }

    private <R, C extends PgsenderConfig> //
    PgsenderMetricRunRelaList(PgsenderActionDataRelaList<R, C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = PgsenderReportRelaList.of(action, finish);
    }

    public final PgsenderReportRelaList relalist;
}

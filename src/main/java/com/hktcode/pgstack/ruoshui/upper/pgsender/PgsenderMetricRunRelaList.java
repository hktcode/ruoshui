/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricRunRelaList implements PgsenderMetricRun
{
    static <C extends PgsenderConfig> //
    PgsenderMetricRunRelaList of(PgsenderActionDataRelaList<C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunRelaList(action);
    }

    private <C extends PgsenderConfig> //
    PgsenderMetricRunRelaList(PgsenderActionDataRelaList<C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = PgsenderReportRelaList.of(action, finish);
    }

    public final PgsenderReportRelaList relalist;
}

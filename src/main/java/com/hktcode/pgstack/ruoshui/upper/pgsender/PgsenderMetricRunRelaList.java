/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricRunRelaList implements PgsenderMetricRun
{
    static  //
    PgsenderMetricRunRelaList of(PgsenderActionDataRelaList action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunRelaList(action);
    }

    private  //
    PgsenderMetricRunRelaList(PgsenderActionDataRelaList action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = PgsenderReportRelaList.of(action, finish);
    }

    public final PgsenderReportRelaList relalist;
}

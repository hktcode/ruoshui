/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class PgsenderMetricRunTypelist implements PgsenderMetricRun
{
    protected PgsenderMetricRunTypelist(PgsenderActionDataTypelist action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = PgsenderReportTypelist.of(action, finish);
    }

    public final PgsenderReportTypelist typelist;
}

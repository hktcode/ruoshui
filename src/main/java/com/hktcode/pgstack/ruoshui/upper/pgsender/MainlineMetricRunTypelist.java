/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class MainlineMetricRunTypelist implements PgsenderMetricRun
{
    protected MainlineMetricRunTypelist(MainlineActionDataTypelist action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = MainlineReportTypelist.of(action, finish);
    }

    public final MainlineReportTypelist typelist;
}

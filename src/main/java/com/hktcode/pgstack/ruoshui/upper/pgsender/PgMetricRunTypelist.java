/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class PgMetricRunTypelist implements PgMetricRun
{
    protected PgMetricRunTypelist(PgActionDataTypelist action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = PgReportTypelist.of(action, finish);
    }

    public final PgReportTypelist typelist;
}

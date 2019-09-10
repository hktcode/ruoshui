/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class PgMetricEndTypelist implements PgMetricEnd
{
    protected PgMetricEndTypelist(PgActionDataTypelist action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = PgReportTypelist.of(action, finish);
        this.complete = PgReportComplete.of(finish);
    }

    public final PgReportTypelist typelist;

    public final PgReportComplete complete;
}

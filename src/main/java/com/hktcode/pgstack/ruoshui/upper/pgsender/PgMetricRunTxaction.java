/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class PgMetricRunTxaction implements PgMetricRun
{
    protected PgMetricRunTxaction(PgActionReplTxaction action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = action.typelist;
        this.txaction = PgReportTxaction.of(action, finish);
    }

    public final PgReportTypelist typelist;

    public final PgReportTxaction txaction;
}

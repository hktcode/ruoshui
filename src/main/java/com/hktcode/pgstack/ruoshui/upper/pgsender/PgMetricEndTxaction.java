/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class PgMetricEndTxaction implements PgMetricEnd
{
    protected PgMetricEndTxaction(PgActionReplTxaction action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = action.typelist;
        this.txaction = PgReportTxaction.of(action, finish);
        this.complete = PgReportComplete.of(finish);
    }

    public final PgReportTypelist typelist;

    public final PgReportTxaction txaction;

    public final PgReportComplete complete;
}

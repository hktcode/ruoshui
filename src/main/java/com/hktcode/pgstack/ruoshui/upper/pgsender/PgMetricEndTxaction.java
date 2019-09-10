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
        this.txaction = PgsenderReportTxaction.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    public final PgsenderReportTypelist typelist;

    public final PgsenderReportTxaction txaction;

    public final PgsenderReportComplete complete;
}

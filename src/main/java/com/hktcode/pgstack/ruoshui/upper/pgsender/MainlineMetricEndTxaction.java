/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class MainlineMetricEndTxaction implements PgsenderMetricEnd
{
    protected MainlineMetricEndTxaction(MainlineActionReplTxaction action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = action.typelist;
        this.txaction = MainlineReportTxaction.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    public final MainlineReportTypelist typelist;

    public final MainlineReportTxaction txaction;

    public final PgsenderReportComplete complete;

    @Override
    public abstract MainlineMetricErrTxaction toErrMetrics(Throwable throwerr);
}

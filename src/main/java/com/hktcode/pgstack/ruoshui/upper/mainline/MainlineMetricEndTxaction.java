/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

public abstract class MainlineMetricEndTxaction implements MainlineMetricEnd
{
    protected MainlineMetricEndTxaction(MainlineActionReplTxaction action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = action.typelist;
        this.txaction = MainlineReportTxaction.of(action, finish);
        this.complete = MainlineReportComplete.of(finish);
    }

    public final MainlineReportTypelist typelist;

    public final MainlineReportTxaction txaction;

    public final MainlineReportComplete complete;

    @Override
    public abstract MainlineMetricErrTxaction toErrMetrics(Throwable throwerr);
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class MainlineMetricRunTxaction implements PgsenderMetricRun
{
    protected MainlineMetricRunTxaction(MainlineActionReplTxaction action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = action.typelist;
        this.txaction = MainlineReportTxaction.of(action, finish);
    }

    public final MainlineReportTypelist typelist;

    public final MainlineReportTxaction txaction;
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public abstract class PgsenderMetricRunTxaction implements PgsenderMetricRun
{
    protected PgsenderMetricRunTxaction(PgsenderActionReplTxaction action)
    {
        long finish = System.currentTimeMillis();
        this.typelist = action.typelist;
        this.txaction = PgsenderReportTxaction.of(action, finish);
    }

    public final PgsenderReportTypelist typelist;

    public final PgsenderReportTxaction txaction;
}

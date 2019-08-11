/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import org.postgresql.replication.LogSequenceNumber;

public abstract class MainlineMetricRunTxaction implements MainlineMetricRun
{
    protected MainlineMetricRunTxaction(MainlineActionReplTxaction action)
    {
        this.begin1st = action.begin1st;
        long finish = System.currentTimeMillis();
        this.typelist = action.typelist;
        this.txaction = MainlineReportTxaction.of(action, finish);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportTypelist typelist;

    public final MainlineReportTxaction txaction;
}

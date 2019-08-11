/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricRunReplSlot implements MainlineMetricRun
{
    static MainlineMetricRunReplSlot of(MainlineActionDataReplSlot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricRunReplSlot(action);
    }

    private MainlineMetricRunReplSlot(MainlineActionDataReplSlot action)
    {
        long finish = System.currentTimeMillis();
        this.begin1st = action.begin1st;
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = MainlineReportReplSlot.of(action, finish);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportReplSlot replslot;
}

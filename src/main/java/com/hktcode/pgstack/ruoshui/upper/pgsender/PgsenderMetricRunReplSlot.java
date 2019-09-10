/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricRunReplSlot implements PgsenderMetricRun
{
    static PgsenderMetricRunReplSlot of(PgActionDataReplSlot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunReplSlot(action);
    }

    private PgsenderMetricRunReplSlot(PgActionDataReplSlot action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = PgsenderReportReplSlot.of(action, finish);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportReplSlot replslot;
}

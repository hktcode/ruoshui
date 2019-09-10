/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricEndReplSlot implements PgMetricEnd
{
    static PgMetricEndReplSlot of(PgActionDataReplSlot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricEndReplSlot(action);
    }

    private PgMetricEndReplSlot(PgActionDataReplSlot action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = PgReportReplSlot.of(action, finish);
        this.complete = PgReportComplete.of(finish);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relalock;

    public final PgReportReplSlot replslot;

    public final PgReportComplete complete;

    @Override
    public PgMetricErrReplSlot toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgMetricErrReplSlot.of(this, throwerr);
    }
}

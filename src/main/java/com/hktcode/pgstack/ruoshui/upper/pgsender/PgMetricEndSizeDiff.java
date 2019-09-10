/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricEndSizeDiff implements PgMetricEnd
{
    static PgMetricEndSizeDiff of(PgActionDataSizeDiff action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricEndSizeDiff(action);
    }

    private PgMetricEndSizeDiff(PgActionDataSizeDiff action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = PgReportSizeDiff.of(action, finish);
        this.complete = PgReportComplete.of(finish);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relalock;

    public final PgReportReplSlot replslot;

    public final PgReportSizeDiff sizediff;

    public final PgReportComplete complete;

    @Override
    public PgMetricErrSizeDiff toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgMetricErrSizeDiff.of(this, throwerr);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricEndSizeDiff implements PgsenderMetricEnd
{
    static <R, C extends PgsenderConfig>
    PgsenderMetricEndSizeDiff of(PgsenderActionDataSizeDiff<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricEndSizeDiff(action);
    }

    private <R, C extends PgsenderConfig>
    PgsenderMetricEndSizeDiff(PgsenderActionDataSizeDiff<R, C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = PgsenderReportSizeDiff.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportReplSlot replslot;

    public final PgsenderReportSizeDiff sizediff;

    public final PgsenderReportComplete complete;

    @Override
    public PgsenderMetricErrSizeDiff toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgsenderMetricErrSizeDiff.of(this, throwerr);
    }
}

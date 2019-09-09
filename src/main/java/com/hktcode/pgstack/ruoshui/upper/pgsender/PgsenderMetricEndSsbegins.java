/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricEndSsbegins implements PgsenderMetricEnd
{
    static <R, C extends PgsenderConfig<R, C>> //
    PgsenderMetricEndSsbegins of(PgsenderActionDataSsBegins<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricEndSsbegins(action);
    }

    private <R, C extends PgsenderConfig<R, C>> //
    PgsenderMetricEndSsbegins(PgsenderActionDataSsBegins<R, C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = PgsenderReportSsBegins.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportReplSlot replslot;

    public final PgsenderReportSizeDiff sizediff;

    public final PgsenderReportSsBegins ssbegins;

    public final PgsenderReportComplete complete;

    @Override
    public PgsenderMetricErrSsbegins toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgsenderMetricErrSsbegins.of(this, throwerr);
    }
}


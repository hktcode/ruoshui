/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricEndSsFinish implements PgsenderMetricEnd
{
    static <R, C extends PgsenderConfig<R, C>>
    PgsenderMetricEndSsFinish of(PgsenderActionDataSsFinish<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricEndSsFinish(action);
    }

    private <R, C extends PgsenderConfig<R, C>>
    PgsenderMetricEndSsFinish(PgsenderActionDataSsFinish<R, C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssBegins;
        this.tupleval = action.tupleval;
        this.ssfinish = PgsenderReportSsFinish.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportReplSlot replslot;

    public final PgsenderReportSizeDiff sizediff;

    public final PgsenderReportSsBegins ssbegins;

    public final PgsenderReportTupleval tupleval;

    public final PgsenderReportSsFinish ssfinish;

    public final PgsenderReportComplete complete;

    @Override
    public PgsenderMetricErrSsFinish toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgsenderMetricErrSsFinish.of(this, throwerr);
    }
}

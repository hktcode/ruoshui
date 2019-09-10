/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricEndTupleval implements PgsenderMetricEnd
{
    static <C extends PgsenderConfig> //
    PgsenderMetricEndTupleval of(PgsenderActionDataTupleval<C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricEndTupleval(action);
    }

    static <C extends PgsenderConfig> //
    PgsenderMetricEndTupleval of(PgsenderActionDataSrBegins<C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricEndTupleval(action);
    }

    static <C extends PgsenderConfig> //
    PgsenderMetricEndTupleval of(PgsenderActionDataSrFinish<C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricEndTupleval(action);
    }

    private <C extends PgsenderConfig>
    PgsenderMetricEndTupleval(PgsenderActionDataTupleval<C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgsenderReportTupleval.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    private <C extends PgsenderConfig>
    PgsenderMetricEndTupleval(PgsenderActionDataSrBegins<C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgsenderReportTupleval.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    private <C extends PgsenderConfig> //
    PgsenderMetricEndTupleval(PgsenderActionDataSrFinish<C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgsenderReportTupleval.of(action, finish);
        this.complete = PgsenderReportComplete.of(finish);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportReplSlot replslot;

    public final PgsenderReportSizeDiff sizediff;

    public final PgsenderReportSsBegins ssbegins;

    public final PgsenderReportTupleval tupleval;

    public final PgsenderReportComplete complete;

    @Override
    public PgsenderMetricErrTupleval toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgsenderMetricErrTupleval.of(this, throwerr);
    }
}

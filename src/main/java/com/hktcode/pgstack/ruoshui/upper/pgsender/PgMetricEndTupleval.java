/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricEndTupleval implements PgMetricEnd
{
    static PgMetricEndTupleval of(PgActionDataTupleval action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricEndTupleval(action);
    }

    static PgMetricEndTupleval of(PgActionDataSrBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricEndTupleval(action);
    }

    static PgMetricEndTupleval of(PgActionDataSrFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricEndTupleval(action);
    }

    private PgMetricEndTupleval(PgActionDataTupleval action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgReportTupleval.of(action, finish);
        this.complete = PgReportComplete.of(finish);
    }

    private PgMetricEndTupleval(PgActionDataSrBegins action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgReportTupleval.of(action, finish);
        this.complete = PgReportComplete.of(finish);
    }

    private PgMetricEndTupleval(PgActionDataSrFinish action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgReportTupleval.of(action, finish);
        this.complete = PgReportComplete.of(finish);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relalock;

    public final PgReportReplSlot replslot;

    public final PgReportSizeDiff sizediff;

    public final PgReportSsBegins ssbegins;

    public final PgReportTupleval tupleval;

    public final PgReportComplete complete;

    @Override
    public PgMetricErrTupleval toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgMetricErrTupleval.of(this, throwerr);
    }
}

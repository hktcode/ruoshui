/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricEndSsFinish implements PgMetricEnd
{
    static PgMetricEndSsFinish of(PgActionDataSsFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricEndSsFinish(action);
    }

    private PgMetricEndSsFinish(PgActionDataSsFinish action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssBegins;
        this.tupleval = action.tupleval;
        this.ssfinish = PgReportSsFinish.of(action, finish);
        this.complete = PgReportComplete.of(finish);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relalock;

    public final PgReportReplSlot replslot;

    public final PgReportSizeDiff sizediff;

    public final PgReportSsBegins ssbegins;

    public final PgReportTupleval tupleval;

    public final PgReportSsFinish ssfinish;

    public final PgReportComplete complete;

    @Override
    public PgMetricErrSsFinish toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgMetricErrSsFinish.of(this, throwerr);
    }
}

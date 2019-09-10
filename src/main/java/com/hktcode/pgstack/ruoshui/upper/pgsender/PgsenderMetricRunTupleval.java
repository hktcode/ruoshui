/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricRunTupleval implements PgsenderMetricRun
{
    static PgsenderMetricRunTupleval of(PgActionDataTupleval action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunTupleval(action);
    }

    static PgsenderMetricRunTupleval of(PgActionDataSrBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunTupleval(action);
    }

    static PgsenderMetricRunTupleval of(PgActionDataSrFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunTupleval(action);
    }

    private PgsenderMetricRunTupleval(PgActionDataTupleval action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgsenderReportTupleval.of(action, finish);
    }

    private PgsenderMetricRunTupleval(PgActionDataSrBegins action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgsenderReportTupleval.of(action, finish);
    }

    private PgsenderMetricRunTupleval(PgActionDataSrFinish action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgsenderReportTupleval.of(action, finish);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportReplSlot replslot;

    public final PgsenderReportSizeDiff sizediff;

    public final PgsenderReportSsBegins ssbegins;

    public final PgsenderReportTupleval tupleval;
}

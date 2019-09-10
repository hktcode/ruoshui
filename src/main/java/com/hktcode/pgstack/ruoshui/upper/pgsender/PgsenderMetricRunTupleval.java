/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricRunTupleval implements PgsenderMetricRun
{
    static <R, C extends PgsenderConfig> //
    PgsenderMetricRunTupleval of(PgsenderActionDataTupleval<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunTupleval(action);
    }

    static <R, C extends PgsenderConfig> //
    PgsenderMetricRunTupleval of(PgsenderActionDataSrBegins<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunTupleval(action);
    }

    static <R, C extends PgsenderConfig>
    PgsenderMetricRunTupleval of(PgsenderActionDataSrFinish<R, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunTupleval(action);
    }

    private <R, C extends PgsenderConfig>
    PgsenderMetricRunTupleval(PgsenderActionDataTupleval<R, C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgsenderReportTupleval.of(action, finish);
    }

    private <R, C extends PgsenderConfig>
    PgsenderMetricRunTupleval(PgsenderActionDataSrBegins<R, C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgsenderReportTupleval.of(action, finish);
    }

    private <R, C extends PgsenderConfig>
    PgsenderMetricRunTupleval(PgsenderActionDataSrFinish<R, C> action)
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

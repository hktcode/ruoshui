/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricRunTupleval implements PgsenderMetricRun
{
    static <C extends PgsenderConfig> //
    PgsenderMetricRunTupleval of(PgsenderActionDataTupleval<C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunTupleval(action);
    }

    static <C extends PgsenderConfig> //
    PgsenderMetricRunTupleval of(PgsenderActionDataSrBegins<C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunTupleval(action);
    }

    static <C extends PgsenderConfig>
    PgsenderMetricRunTupleval of(PgsenderActionDataSrFinish<C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricRunTupleval(action);
    }

    private <C extends PgsenderConfig>
    PgsenderMetricRunTupleval(PgsenderActionDataTupleval<C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgsenderReportTupleval.of(action, finish);
    }

    private <C extends PgsenderConfig>
    PgsenderMetricRunTupleval(PgsenderActionDataSrBegins<C> action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = PgsenderReportTupleval.of(action, finish);
    }

    private <C extends PgsenderConfig>
    PgsenderMetricRunTupleval(PgsenderActionDataSrFinish<C> action)
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

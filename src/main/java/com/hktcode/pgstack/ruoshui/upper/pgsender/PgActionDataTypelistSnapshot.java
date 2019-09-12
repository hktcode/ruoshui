/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

class PgActionDataTypelistSnapshot extends PgActionDataTypelist
{
    static PgActionDataTypelistSnapshot of(PgActionDataSsFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataTypelistSnapshot(action);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relaLock;

    public final PgReportReplSlot replSlot;

    public final PgReportSizeDiff sizeDiff;

    public final PgReportSsBegins ssBegins;

    public final PgReportTupleval tupleval;

    public final PgReportSsFinish ssfinish;

    private PgActionDataTypelistSnapshot(PgActionDataSsFinish action)
    {
        super(action);
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssBegins = action.ssBegins;
        this.tupleval = action.tupleval;
        this.ssfinish = PgReportSsFinish.of(action, this.actionStart);
    }

    public PgActionReplTxaction txaction()
    {
        return PgActionReplTxactionSnapshot.of(this);
    }

    @Override
    public PgMetricRunTypelistSnapshot toRunMetrics()
    {
        return PgMetricRunTypelistSnapshot.of(this);
    }
}


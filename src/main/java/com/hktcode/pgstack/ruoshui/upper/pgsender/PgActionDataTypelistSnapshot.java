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

    final PgReportRelaList relalist;

    final PgReportRelaLock relaLock;

    final PgReportReplSlot replSlot;

    final PgReportSizeDiff sizeDiff;

    final PgReportSsBegins ssBegins;

    final PgReportTupleval tupleval;

    final PgReportSsFinish ssfinish;

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

    @Override
    PgActionReplTxaction txaction()
    {
        return PgActionReplTxactionSnapshot.of(this);
    }

    @Override
    public PgMetricRunTypelistSnapshot toRunMetrics()
    {
        return PgMetricRunTypelistSnapshot.of(this);
    }
}


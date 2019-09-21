/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

class PgActionReplTxactionSnapshot extends PgActionReplTxaction
{
    static PgActionReplTxactionSnapshot of(PgActionDataTypelistSnapshot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionReplTxactionSnapshot(action);
    }

    final PgReportRelaList relalist;

    final PgReportRelaLock relaLock;

    final PgReportReplSlot replSlot;

    final PgReportSizeDiff sizeDiff;

    final PgReportSsBegins ssBegins;

    final PgReportTupleval tupleval;

    final PgReportSsFinish ssfinish;

    private PgActionReplTxactionSnapshot(PgActionDataTypelistSnapshot action)
    {
        super(action);
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssBegins = action.ssBegins;
        this.tupleval = action.tupleval;
        this.ssfinish = action.ssfinish;
    }

    @Override
    public PgMetricRunTxactionSnapshot toRunMetrics()
    {
        return PgMetricRunTxactionSnapshot.of(this);
    }
}


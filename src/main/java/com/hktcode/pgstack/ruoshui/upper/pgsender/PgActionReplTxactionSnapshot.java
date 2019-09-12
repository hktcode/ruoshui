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

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relaLock;

    public final PgReportReplSlot replSlot;

    public final PgReportSizeDiff sizeDiff;

    public final PgReportSsBegins ssBegins;

    public final PgReportTupleval tupleval;

    public final PgReportSsFinish ssfinish;

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


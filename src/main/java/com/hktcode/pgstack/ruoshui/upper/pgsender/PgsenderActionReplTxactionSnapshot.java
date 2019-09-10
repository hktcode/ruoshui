/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

class PgsenderActionReplTxactionSnapshot extends PgsenderActionReplTxaction
{
    static PgsenderActionReplTxactionSnapshot of(PgsenderActionDataTypelistSnapshot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionReplTxactionSnapshot(action);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relaLock;

    public final PgsenderReportReplSlot replSlot;

    public final PgsenderReportSizeDiff sizeDiff;

    public final PgsenderReportSsBegins ssBegins;

    public final PgsenderReportTupleval tupleval;

    public final PgsenderReportSsFinish ssfinish;

    private PgsenderActionReplTxactionSnapshot(PgsenderActionDataTypelistSnapshot action)
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
    public PgsenderMetricRunTxactionSnapshot toRunMetrics()
    {
        return PgsenderMetricRunTxactionSnapshot.of(this);
    }

    @Override
    public PgsenderMetricEndTxactionSnapshot toEndMetrics()
    {
        return PgsenderMetricEndTxactionSnapshot.of(this);
    }
}


/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

class MainlineActionReplTxactionSnapshot extends MainlineActionReplTxaction
{
    static MainlineActionReplTxactionSnapshot of(MainlineActionDataTypelistSnapshot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionReplTxactionSnapshot(action);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relaLock;

    public final PgsenderReportReplSlot replSlot;

    public final PgsenderReportSizeDiff sizeDiff;

    public final PgsenderReportSsBegins ssBegins;

    public final PgsenderReportTupleval tupleval;

    public final PgsenderReportSsFinish ssfinish;

    private MainlineActionReplTxactionSnapshot(MainlineActionDataTypelistSnapshot action)
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

    public MainlineMetricRunTxactionSnapshot complete()
    {
        return MainlineMetricRunTxactionSnapshot.of(this);
    }

    @Override
    public MainlineMetricRunTxactionSnapshot toRunMetrics()
    {
        return MainlineMetricRunTxactionSnapshot.of(this);
    }

    @Override
    public MainlineMetricEndTxactionSnapshot toEndMetrics()
    {
        return MainlineMetricEndTxactionSnapshot.of(this);
    }
}


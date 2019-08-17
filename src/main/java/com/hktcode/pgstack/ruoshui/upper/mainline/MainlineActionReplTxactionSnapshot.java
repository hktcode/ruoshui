/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

class MainlineActionReplTxactionSnapshot //
    extends MainlineActionReplTxaction
{
    static MainlineActionReplTxactionSnapshot of(MainlineActionDataTypelistSnapshot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionReplTxactionSnapshot(action);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relaLock;

    public final MainlineReportReplSlot replSlot;

    public final MainlineReportSizeDiff sizeDiff;

    public final MainlineReportSsBegins ssBegins;

    public final MainlineReportTupleval tupleval;

    public final MainlineReportSsFinish ssfinish;

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


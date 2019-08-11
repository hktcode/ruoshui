/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

class MainlineActionDataTypelistSnapshot //
    extends MainlineActionDataTypelist<MainlineActionDataTypelistSnapshot>
{
    static MainlineActionDataTypelistSnapshot of(MainlineActionDataSsFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionDataTypelistSnapshot(action);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relaLock;

    public final MainlineReportReplSlot replSlot;

    public final MainlineReportSizeDiff sizeDiff;

    public final MainlineReportSsBegins ssBegins;

    public final MainlineReportTupleval tupleval;

    public final MainlineReportSsFinish ssfinish;

    private MainlineActionDataTypelistSnapshot(MainlineActionDataSsFinish action)
    {
        super(action);
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssBegins = action.ssBegins;
        this.tupleval = action.tupleval;
        this.ssfinish = MainlineReportSsFinish.of(action, this.actionStart);
    }

    public MainlineActionReplTxaction txaction()
    {
        return MainlineActionReplTxactionSnapshot.of(this);
    }

    @Override
    public MainlineMetricRunTypelistSnapshot toRunMetrics()
    {
        return MainlineMetricRunTypelistSnapshot.of(this);
    }

    @Override
    public MainlineMetricEndTypelistSnapshot toEndMetrics()
    {
        return MainlineMetricEndTypelistSnapshot.of(this);
    }
}


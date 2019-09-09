/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;

class MainlineActionDataTypelistSnapshot extends MainlineActionDataTypelist
{
    static MainlineActionDataTypelistSnapshot //
    of(PgsenderActionDataSsFinish<MainlineRecord, MainlineConfig> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionDataTypelistSnapshot(action);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relaLock;

    public final PgsenderReportReplSlot replSlot;

    public final PgsenderReportSizeDiff sizeDiff;

    public final PgsenderReportSsBegins ssBegins;

    public final PgsenderReportTupleval tupleval;

    public final PgsenderReportSsFinish ssfinish;

    private MainlineActionDataTypelistSnapshot
        (PgsenderActionDataSsFinish<MainlineRecord, MainlineConfig> action)
    {
        super(action);
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
        this.sizeDiff = action.sizeDiff;
        this.ssBegins = action.ssBegins;
        this.tupleval = action.tupleval;
        this.ssfinish = PgsenderReportSsFinish.of(action, this.actionStart);
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


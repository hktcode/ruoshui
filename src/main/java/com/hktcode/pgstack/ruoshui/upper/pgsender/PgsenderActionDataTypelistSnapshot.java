/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;

class PgsenderActionDataTypelistSnapshot extends PgsenderActionDataTypelist
{
    static PgsenderActionDataTypelistSnapshot //
    of(PgsenderActionDataSsFinish<MainlineRecord, MainlineConfig> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionDataTypelistSnapshot(action);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relaLock;

    public final PgsenderReportReplSlot replSlot;

    public final PgsenderReportSizeDiff sizeDiff;

    public final PgsenderReportSsBegins ssBegins;

    public final PgsenderReportTupleval tupleval;

    public final PgsenderReportSsFinish ssfinish;

    private PgsenderActionDataTypelistSnapshot
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

    public PgsenderActionReplTxaction txaction()
    {
        return PgsenderActionReplTxactionSnapshot.of(this);
    }

    @Override
    public PgsenderMetricRunTypelistSnapshot toRunMetrics()
    {
        return PgsenderMetricRunTypelistSnapshot.of(this);
    }

    @Override
    public PgsenderMetricEndTypelistSnapshot toEndMetrics()
    {
        return PgsenderMetricEndTypelistSnapshot.of(this);
    }
}


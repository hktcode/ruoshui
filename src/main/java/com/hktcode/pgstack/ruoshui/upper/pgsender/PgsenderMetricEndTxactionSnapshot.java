/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricEndTxactionSnapshot extends PgsenderMetricEndTxaction
{
    static PgsenderMetricEndTxactionSnapshot of(PgsenderActionReplTxactionSnapshot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricEndTxactionSnapshot(action);
    }

    private PgsenderMetricEndTxactionSnapshot(PgsenderActionReplTxactionSnapshot action)
    {
        super(action);
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssBegins;
        this.tupleval = action.tupleval;
        this.ssfinish = action.ssfinish;
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportReplSlot replslot;

    public final PgsenderReportSizeDiff sizediff;

    public final PgsenderReportSsBegins ssbegins;

    public final PgsenderReportTupleval tupleval;

    public final PgsenderReportSsFinish ssfinish;

    @Override
    public PgsenderMetricErrTxactionSnapshot toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgsenderMetricErrTxactionSnapshot.of(this, throwerr);
    }
}

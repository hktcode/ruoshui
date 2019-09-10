/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricEndTxactionSnapshot extends PgMetricEndTxaction
{
    static PgMetricEndTxactionSnapshot of(PgActionReplTxactionSnapshot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricEndTxactionSnapshot(action);
    }

    private PgMetricEndTxactionSnapshot(PgActionReplTxactionSnapshot action)
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

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relalock;

    public final PgReportReplSlot replslot;

    public final PgReportSizeDiff sizediff;

    public final PgReportSsBegins ssbegins;

    public final PgReportTupleval tupleval;

    public final PgReportSsFinish ssfinish;

    @Override
    public PgMetricErrTxactionSnapshot toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgMetricErrTxactionSnapshot.of(this, throwerr);
    }
}

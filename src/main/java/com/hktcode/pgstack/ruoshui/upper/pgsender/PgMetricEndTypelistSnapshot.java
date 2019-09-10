/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricEndTypelistSnapshot extends PgMetricEndTypelist
{
    static PgMetricEndTypelistSnapshot of(PgActionDataTypelistSnapshot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricEndTypelistSnapshot(action);
    }

    private PgMetricEndTypelistSnapshot(PgActionDataTypelistSnapshot action)
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
    public PgMetricErrTypelistSnapshot toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgMetricErrTypelistSnapshot.of(this, throwerr);
    }
}

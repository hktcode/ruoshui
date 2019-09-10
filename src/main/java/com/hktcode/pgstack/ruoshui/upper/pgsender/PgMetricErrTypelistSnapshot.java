/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricErrTypelistSnapshot extends PgMetricErrTypelist
{
    static PgMetricErrTypelistSnapshot of(PgMetricEndTypelistSnapshot metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgMetricErrTypelistSnapshot(metric, throwerr);
    }

    private PgMetricErrTypelistSnapshot(PgMetricEndTypelistSnapshot metric, Throwable throwerr)
    {
        super(metric, throwerr);
        this.relalist = metric.relalist;
        this.relalock = metric.relalock;
        this.replslot = metric.replslot;
        this.sizediff = metric.sizediff;
        this.ssbegins = metric.ssbegins;
        this.tupleval = metric.tupleval;
        this.ssfinish = metric.ssfinish;
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relalock;

    public final PgReportReplSlot replslot;

    public final PgReportSizeDiff sizediff;

    public final PgReportSsBegins ssbegins;

    public final PgReportTupleval tupleval;

    public final PgReportSsFinish ssfinish;
}

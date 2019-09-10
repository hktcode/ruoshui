/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricErrTupleval implements PgMetricErr
{
    static PgMetricErrTupleval of(PgMetricEndTupleval metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new PgMetricErrTupleval(metric, throwerr);
    }

    private PgMetricErrTupleval(PgMetricEndTupleval metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.relalist = metric.relalist;
        this.relalock = metric.relalock;
        this.replslot = metric.replslot;
        this.sizediff = metric.sizediff;
        this.ssbegins = metric.ssbegins;
        this.tupleval = metric.tupleval;
        this.throwerr = PgReportThrowErr.of(finish, throwerr);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relalock;

    public final PgReportReplSlot replslot;

    public final PgReportSizeDiff sizediff;

    public final PgReportSsBegins ssbegins;

    public final PgReportTupleval tupleval;

    public final PgReportThrowErr throwerr;
}

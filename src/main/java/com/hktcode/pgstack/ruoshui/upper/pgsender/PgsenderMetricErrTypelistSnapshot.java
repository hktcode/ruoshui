/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricErrTypelistSnapshot extends PgsenderMetricErrTypelist
{
    static PgsenderMetricErrTypelistSnapshot of(PgsenderMetricEndTypelistSnapshot metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgsenderMetricErrTypelistSnapshot(metric, throwerr);
    }

    private PgsenderMetricErrTypelistSnapshot(PgsenderMetricEndTypelistSnapshot metric, Throwable throwerr)
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

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportReplSlot replslot;

    public final PgsenderReportSizeDiff sizediff;

    public final PgsenderReportSsBegins ssbegins;

    public final PgsenderReportTupleval tupleval;

    public final PgsenderReportSsFinish ssfinish;
}

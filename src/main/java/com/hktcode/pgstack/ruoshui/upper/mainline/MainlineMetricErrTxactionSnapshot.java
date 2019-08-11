/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrTxactionSnapshot extends MainlineMetricErrTxaction
{
    static MainlineMetricErrTxactionSnapshot of(MainlineMetricEndTxactionSnapshot metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new MainlineMetricErrTxactionSnapshot(metric, throwerr);
    }

    private MainlineMetricErrTxactionSnapshot(MainlineMetricEndTxactionSnapshot metric, Throwable throwerr)
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

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportReplSlot replslot;

    public final MainlineReportSizeDiff sizediff;

    public final MainlineReportSsBegins ssbegins;

    public final MainlineReportTupleval tupleval;

    public final MainlineReportSsFinish ssfinish;
}

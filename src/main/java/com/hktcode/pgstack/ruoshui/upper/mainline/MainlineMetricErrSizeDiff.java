/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrSizeDiff implements MainlineMetricErr
{
    static MainlineMetricErrSizeDiff of(MainlineMetricEndSizeDiff metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("action");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new MainlineMetricErrSizeDiff(metric, throwerr);
    }

    private MainlineMetricErrSizeDiff(MainlineMetricEndSizeDiff metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.begin1st = metric.begin1st;
        this.relalist = metric.relalist;
        this.relalock = metric.relalock;
        this.replslot = metric.replslot;
        this.sizediff = metric.sizediff;
        this.throwerr = MainlineReportThrowErr.of(finish, throwerr);
    }

    public final MainlineReportBegin1st begin1st;

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relalock;

    public final MainlineReportReplSlot replslot;

    public final MainlineReportSizeDiff sizediff;

    public final MainlineReportThrowErr throwerr;
}

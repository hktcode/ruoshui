/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricErrSizeDiff implements PgsenderMetricErr
{
    static PgsenderMetricErrSizeDiff of(PgsenderMetricEndSizeDiff metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("action");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new PgsenderMetricErrSizeDiff(metric, throwerr);
    }

    private PgsenderMetricErrSizeDiff(PgsenderMetricEndSizeDiff metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.relalist = metric.relalist;
        this.relalock = metric.relalock;
        this.replslot = metric.replslot;
        this.sizediff = metric.sizediff;
        this.throwerr = PgsenderReportThrowErr.of(finish, throwerr);
    }

    public final PgsenderReportRelaList relalist;

    public final PgsenderReportRelaLock relalock;

    public final PgsenderReportReplSlot replslot;

    public final PgsenderReportSizeDiff sizediff;

    public final PgsenderReportThrowErr throwerr;
}

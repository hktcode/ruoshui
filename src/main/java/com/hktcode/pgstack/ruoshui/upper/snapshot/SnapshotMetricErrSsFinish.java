/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotMetricErrSsFinish implements SnapshotMetricErr
{
    static SnapshotMetricErrSsFinish of(SnapshotMetricEndSsFinish metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new SnapshotMetricErrSsFinish(metric, throwerr);
    }

    private SnapshotMetricErrSsFinish(SnapshotMetricEndSsFinish metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.relalist = metric.relalist;
        this.relalock = metric.relalock;
        this.replslot = metric.replslot;
        this.sizediff = metric.sizediff;
        this.ssbegins = metric.ssbegins;
        this.tupleval = metric.tupleval;
        this.ssfinish = metric.ssfinish;
        this.throwerr = SnapshotReportThrowErr.of(finish, throwerr);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relalock;

    public final SnapshotReportReplSlot replslot;

    public final SnapshotReportSizeDiff sizediff;

    public final SnapshotReportSsBegins ssbegins;

    public final SnapshotReportTupleval tupleval;

    public final SnapshotReportSsFinish ssfinish;

    public final SnapshotReportThrowErr throwerr;
}

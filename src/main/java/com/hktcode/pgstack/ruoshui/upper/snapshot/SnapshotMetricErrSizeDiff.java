/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotMetricErrSizeDiff implements SnapshotMetricErr
{
    static SnapshotMetricErrSizeDiff of(SnapshotMetricEndSizeDiff metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("action");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new SnapshotMetricErrSizeDiff(metric, throwerr);
    }

    private SnapshotMetricErrSizeDiff(SnapshotMetricEndSizeDiff metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.relalist = metric.relalist;
        this.relalock = metric.relalock;
        this.replslot = metric.replslot;
        this.sizediff = metric.sizediff;
        this.throwerr = SnapshotReportThrowErr.of(finish, throwerr);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relalock;

    public final SnapshotReportReplSlot replslot;

    public final SnapshotReportSizeDiff sizediff;

    public final SnapshotReportThrowErr throwerr;
}

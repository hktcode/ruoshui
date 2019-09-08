/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotMetricEndSizeDiff implements SnapshotMetricEnd
{
    static SnapshotMetricEndSizeDiff of(SnapshotActionDataSizeDiff action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotMetricEndSizeDiff(action);
    }

    private SnapshotMetricEndSizeDiff(SnapshotActionDataSizeDiff action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = SnapshotReportSizeDiff.of(action, finish);
        this.complete = SnapshotReportComplete.of(finish);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relalock;

    public final SnapshotReportReplSlot replslot;

    public final SnapshotReportSizeDiff sizediff;

    public final SnapshotReportComplete complete;

    @Override
    public SnapshotMetricErrSizeDiff toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return SnapshotMetricErrSizeDiff.of(this, throwerr);
    }
}

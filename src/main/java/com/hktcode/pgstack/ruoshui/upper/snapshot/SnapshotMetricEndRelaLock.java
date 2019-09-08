/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotMetricEndRelaLock implements SnapshotMetricEnd
{
    static SnapshotMetricEndRelaLock of(SnapshotActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotMetricEndRelaLock(action);
    }

    private SnapshotMetricEndRelaLock(SnapshotActionDataRelaLock action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = SnapshotReportRelaLock.of(action, finish);
        this.complete = SnapshotReportComplete.of(finish);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relalock;

    public final SnapshotReportComplete complete;

    @Override
    public SnapshotMetricErrRelaLock toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return SnapshotMetricErrRelaLock.of(this, throwerr);
    }
}

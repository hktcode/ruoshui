/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotMetricEndRelaList implements SnapshotMetricEnd
{
    static SnapshotMetricEndRelaList of(SnapshotActionDataRelaList action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotMetricEndRelaList(action);
    }

    private SnapshotMetricEndRelaList(SnapshotActionDataRelaList action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = SnapshotReportRelaList.of(action, finish);
        this.complete = SnapshotReportComplete.of(finish);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportComplete complete;

    @Override
    public SnapshotMetricErrRelaList toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return SnapshotMetricErrRelaList.of(this, throwerr);
    }
}

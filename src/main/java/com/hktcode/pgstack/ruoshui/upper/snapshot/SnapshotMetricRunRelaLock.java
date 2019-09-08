/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotMetricRunRelaLock implements SnapshotMetricRun
{
    static SnapshotMetricRunRelaLock of(SnapshotActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotMetricRunRelaLock(action);
    }

    private SnapshotMetricRunRelaLock(SnapshotActionDataRelaLock action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = SnapshotReportRelaLock.of(action, finish);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relalock;
}

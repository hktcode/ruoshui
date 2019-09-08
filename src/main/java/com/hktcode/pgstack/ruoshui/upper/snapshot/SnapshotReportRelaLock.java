/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotReportRelaLock implements SnapshotReport
{
    static SnapshotReportRelaLock of(SnapshotActionDataRelaLock action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotReportRelaLock(action, finish);
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    private SnapshotReportRelaLock(SnapshotActionDataRelaLock action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
    }
}

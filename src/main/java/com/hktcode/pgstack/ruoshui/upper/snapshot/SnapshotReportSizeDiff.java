/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotReportSizeDiff implements SnapshotReport
{
    static SnapshotReportSizeDiff of(SnapshotActionDataSizeDiff action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotReportSizeDiff(action, finish);
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    public final long newRelalist;

    private SnapshotReportSizeDiff(SnapshotActionDataSizeDiff action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
        this.newRelalist = action.rsnextCount;
    }
}

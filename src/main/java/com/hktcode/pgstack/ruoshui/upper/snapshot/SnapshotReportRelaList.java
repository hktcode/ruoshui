/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotReportRelaList implements SnapshotReport
{
    public static SnapshotReportRelaList of //
        /* */( long totalMillis //
        /* */, long rsgetCounts //
        /* */, long rsgetMillis //
        /* */, long rsnextCount //
        /* */, long relationLst //
        /* */, ImmutableList<String> retryReason //
        /* */) //
    {
        if (retryReason == null) {
            throw new ArgumentNullException("retryReason");
        }
        return new SnapshotReportRelaList //
            /* */( totalMillis //
            /* */, rsgetCounts //
            /* */, rsgetMillis //
            /* */, rsnextCount //
            /* */, relationLst //
            /* */, retryReason //
            /* */);
    }

    static SnapshotReportRelaList of(SnapshotActionDataRelaList action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotReportRelaList(action, finish);
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    public final long relationLst;

    public final ImmutableList<String> retryReason;

    private SnapshotReportRelaList(SnapshotActionDataRelaList action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
        this.retryReason = action.retryReason;
        this.relationLst = action.relationLst.size();
    }

    private SnapshotReportRelaList //
        /* */( long totalMillis //
        /* */, long rsgetCounts //
        /* */, long rsgetMillis //
        /* */, long rsnextCount //
        /* */, long relationLst //
        /* */, ImmutableList<String> retryReason //
        /* */) //
    {
        this.totalMillis = totalMillis;
        this.rsgetCounts = rsgetCounts;
        this.rsgetMillis = rsgetMillis;
        this.rsnextCount = rsnextCount;
        this.retryReason = retryReason;
        this.relationLst = relationLst;
    }
}

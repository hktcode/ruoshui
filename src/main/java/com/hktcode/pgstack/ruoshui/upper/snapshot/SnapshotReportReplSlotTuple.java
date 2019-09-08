/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;

public class SnapshotReportReplSlotTuple extends SnapshotReportReplSlot
{
    static SnapshotReportReplSlotTuple of(SnapshotActionDataReplSlot action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotReportReplSlotTuple(action, finish);
    }

    public final PgReplSlotTuple createTuple;

    private SnapshotReportReplSlotTuple(SnapshotActionDataReplSlot action, long finish)
    {
        super(action, finish);
        this.createTuple = action.createTuple[0];
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotReportReplSlotEmpty extends SnapshotReportReplSlot
{
    static SnapshotReportReplSlotEmpty of(SnapshotActionDataReplSlot action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotReportReplSlotEmpty(action, finish);
    }

    private SnapshotReportReplSlotEmpty(SnapshotActionDataReplSlot action, long finish)
    {
        super(action, finish);
    }
}

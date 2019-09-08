/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class SnapshotReportComplete implements SnapshotReport
{
    public final long actionStart;

    static SnapshotReportComplete of(long actionStart)
    {
        return new SnapshotReportComplete(actionStart);
    }

    private SnapshotReportComplete(long actionStart)
    {
        this.actionStart = actionStart;
    }
}

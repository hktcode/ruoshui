/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotMetricRunRelaList implements SnapshotMetricRun
{
    static SnapshotMetricRunRelaList of(SnapshotActionDataRelaList action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotMetricRunRelaList(action);
    }

    private SnapshotMetricRunRelaList(SnapshotActionDataRelaList action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = SnapshotReportRelaList.of(action, finish);
    }

    public final SnapshotReportRelaList relalist;
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotMetricRunSsbegins implements SnapshotMetricRun
{
    static SnapshotMetricRunSsbegins of(SnapshotActionDataSsBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotMetricRunSsbegins(action);
    }

    private SnapshotMetricRunSsbegins(SnapshotActionDataSsBegins action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = SnapshotReportSsBegins.of(action, finish);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relalock;

    public final SnapshotReportReplSlot replslot;

    public final SnapshotReportSizeDiff sizediff;

    public final SnapshotReportSsBegins ssbegins;
}

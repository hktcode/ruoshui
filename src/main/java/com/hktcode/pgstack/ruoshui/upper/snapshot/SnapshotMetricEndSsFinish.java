/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotMetricEndSsFinish implements SnapshotMetricEnd
{
    static SnapshotMetricEndSsFinish of(SnapshotActionDataSsFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotMetricEndSsFinish(action);
    }

    private SnapshotMetricEndSsFinish(SnapshotActionDataSsFinish action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssBegins;
        this.tupleval = action.tupleval;
        this.ssfinish = SnapshotReportSsFinish.of(action, finish);
        this.complete = SnapshotReportComplete.of(finish);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relalock;

    public final SnapshotReportReplSlot replslot;

    public final SnapshotReportSizeDiff sizediff;

    public final SnapshotReportSsBegins ssbegins;

    public final SnapshotReportTupleval tupleval;

    public final SnapshotReportSsFinish ssfinish;

    public final SnapshotReportComplete complete;

    @Override
    public SnapshotMetricErrSsFinish toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return SnapshotMetricErrSsFinish.of(this, throwerr);
    }
}

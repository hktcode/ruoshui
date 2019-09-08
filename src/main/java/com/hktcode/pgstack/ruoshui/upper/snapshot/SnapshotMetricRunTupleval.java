/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotMetricRunTupleval implements SnapshotMetricRun
{
    static SnapshotMetricRunTupleval of(SnapshotActionDataTupleval action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotMetricRunTupleval(action);
    }

    static SnapshotMetricRunTupleval of(SnapshotActionDataSrBegins action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotMetricRunTupleval(action);
    }

    static SnapshotMetricRunTupleval of(SnapshotActionDataSrFinish action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotMetricRunTupleval(action);
    }

    private SnapshotMetricRunTupleval(SnapshotActionDataTupleval action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = SnapshotReportTupleval.of(action, finish);
    }

    private SnapshotMetricRunTupleval(SnapshotActionDataSrBegins action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = SnapshotReportTupleval.of(action, finish);
    }

    private SnapshotMetricRunTupleval(SnapshotActionDataSrFinish action)
    {
        long finish = System.currentTimeMillis();
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
        this.sizediff = action.sizeDiff;
        this.ssbegins = action.ssbegins;
        this.tupleval = SnapshotReportTupleval.of(action, finish);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportRelaLock relalock;

    public final SnapshotReportReplSlot replslot;

    public final SnapshotReportSizeDiff sizediff;

    public final SnapshotReportSsBegins ssbegins;

    public final SnapshotReportTupleval tupleval;
}

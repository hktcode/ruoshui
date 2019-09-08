/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotMetricErrRelaList implements SnapshotMetricErr
{
    static SnapshotMetricErrRelaList //
    of(SnapshotMetricEndRelaList metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new SnapshotMetricErrRelaList(metric, throwerr);
    }

    private SnapshotMetricErrRelaList(SnapshotMetricEndRelaList metric, Throwable throwerr)
    {
        long finish = System.currentTimeMillis();
        this.relalist = metric.relalist;
        this.throwerr = SnapshotReportThrowErr.of(finish, throwerr);
    }

    public final SnapshotReportRelaList relalist;

    public final SnapshotReportThrowErr throwerr;
}

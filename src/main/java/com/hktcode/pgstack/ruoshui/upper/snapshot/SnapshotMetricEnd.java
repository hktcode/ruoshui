/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

public interface SnapshotMetricEnd extends SnapshotMetric
{
    SnapshotMetricErr toErrMetrics(Throwable throwerr);
}

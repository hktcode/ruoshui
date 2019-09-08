/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotResultRun implements SnapshotResult
{
    public static SnapshotResultRun of(SnapshotConfig config, SnapshotMetricRun metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new SnapshotResultRun(config, metric);
    }

    public final SnapshotConfig config;

    public final SnapshotMetricRun metric;

    private SnapshotResultRun(SnapshotConfig config, SnapshotMetricRun metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

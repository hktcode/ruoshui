/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotResultErr //
    implements SnapshotResult, SimpleMethodAllResultEnd<SnapshotAction>
{
    public static SnapshotResultErr of(SnapshotConfig config, SnapshotMetricErr metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new SnapshotResultErr(config, metric);
    }

    public final SnapshotConfig config;

    public final SnapshotMetricErr metric;

    private SnapshotResultErr(SnapshotConfig config, SnapshotMetricErr metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

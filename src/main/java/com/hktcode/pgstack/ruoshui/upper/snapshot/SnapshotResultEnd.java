/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;

public class SnapshotResultEnd //
    implements SnapshotResult, SimpleMethodAllResultEnd<SnapshotAction>
{
    public static SnapshotResultEnd of(SnapshotConfig config, SnapshotMetricEnd metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new SnapshotResultEnd(config, metric);
    }

    public final SnapshotConfig config;

    public final SnapshotMetricEnd metric;

    private SnapshotResultEnd(SnapshotConfig config, SnapshotMetricEnd metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

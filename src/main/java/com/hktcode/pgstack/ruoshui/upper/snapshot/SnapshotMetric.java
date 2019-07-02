/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperRunnableMetric;

import java.time.ZonedDateTime;

public class SnapshotMetric extends UpperRunnableMetric
{
    public static SnapshotMetric of(ZonedDateTime startMillis)
    {
        if (startMillis == null) {
            throw new ArgumentNullException("startMillis");
        }
        return new SnapshotMetric(startMillis);
    }

    private SnapshotMetric(ZonedDateTime startMillis)
    {
        super(startMillis);
    }
}

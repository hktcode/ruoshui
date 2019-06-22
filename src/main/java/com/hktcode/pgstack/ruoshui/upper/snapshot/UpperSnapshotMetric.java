/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperRunnableMetric;

import java.time.ZonedDateTime;

public class UpperSnapshotMetric extends UpperRunnableMetric
{
    public static UpperSnapshotMetric of(ZonedDateTime startMillis)
    {
        if (startMillis == null) {
            throw new ArgumentNullException("startMillis");
        }
        return new UpperSnapshotMetric(startMillis);
    }

    private UpperSnapshotMetric(ZonedDateTime startMillis)
    {
        super(startMillis);
    }
}

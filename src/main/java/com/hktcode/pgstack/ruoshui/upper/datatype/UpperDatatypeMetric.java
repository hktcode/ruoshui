/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.datatype;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperRunnableMetric;

import java.time.ZonedDateTime;

public class UpperDatatypeMetric extends UpperRunnableMetric
{
    public static UpperDatatypeMetric of(ZonedDateTime startMillis)
    {
        if (null == startMillis) {
            throw new ArgumentNullException("startMillis");
        }
        return new UpperDatatypeMetric(startMillis);
    }

    private UpperDatatypeMetric(ZonedDateTime startMillis)
    {
        super(startMillis);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class TripleProducerMetric extends TripleMetric
{
    public static TripleProducerMetric of(ZonedDateTime startMillis)
    {
        if (startMillis == null) {
            throw new ArgumentNullException("startMillis");
        }
        return new TripleProducerMetric(startMillis);
    }

    protected TripleProducerMetric(ZonedDateTime createTime)
    {
        super(createTime);
    }
}

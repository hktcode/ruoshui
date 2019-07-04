/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class TripleConsumerMetric extends TripleMetric
{
    public static TripleConsumerMetric of(ZonedDateTime startMillis)
    {
        if (startMillis == null) {
            throw new ArgumentNullException("startMillis");
        }
        return new TripleConsumerMetric(startMillis);
    }

    protected TripleConsumerMetric(ZonedDateTime startMillis)
    {
        super(startMillis);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgtriple.naive;

import java.time.ZonedDateTime;

public class NaiveConsumerMutableMetric extends NaiveMutableMetric
{
    public static NaiveConsumerMutableMetric of()
    {
        ZonedDateTime startMillis = ZonedDateTime.now();
        return new NaiveConsumerMutableMetric(startMillis);
    }

    protected NaiveConsumerMutableMetric(ZonedDateTime startMillis)
    {
        super(startMillis);
    }

    public NaiveConsumerMetric toMetric()
    {
        return NaiveConsumerMetric.of(this);
    }
}

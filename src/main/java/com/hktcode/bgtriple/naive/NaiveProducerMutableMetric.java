/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.naive;

import java.time.ZonedDateTime;

public class NaiveProducerMutableMetric extends NaiveMutableMetric
{
    public static NaiveProducerMutableMetric of()
    {
        ZonedDateTime createTime = ZonedDateTime.now();
        return new NaiveProducerMutableMetric(createTime);
    }

    protected NaiveProducerMutableMetric(ZonedDateTime createTime)
    {
        super(createTime);
    }

    public NaiveProducerMetric toMetric()
    {
        return NaiveProducerMetric.of(this);
    }
}

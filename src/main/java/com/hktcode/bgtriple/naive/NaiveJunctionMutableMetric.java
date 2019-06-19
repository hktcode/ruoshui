/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgtriple.naive;

import java.time.ZonedDateTime;

public class NaiveJunctionMutableMetric extends NaiveMutableMetric
{
    public static NaiveJunctionMutableMetric of()
    {
        ZonedDateTime createTime = ZonedDateTime.now();
        return new NaiveJunctionMutableMetric(createTime);
    }

    public long exeDuration = 0;

    protected NaiveJunctionMutableMetric(ZonedDateTime createTime)
    {
        super(createTime);
    }

    public NaiveJunctionMetric toMetric()
    {
        return NaiveJunctionMetric.of(this);
    }
}

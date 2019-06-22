/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import com.hktcode.bgtriple.naive.NaiveJunctionMutableMetric;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalTxactContext;

import java.time.ZonedDateTime;

public class UpperJunctionMutableMetric extends NaiveJunctionMutableMetric
{
    public static UpperJunctionMutableMetric of()
    {
        ZonedDateTime createTime = ZonedDateTime.now();
        return new UpperJunctionMutableMetric(createTime);
    }

    public long curLsnofcmt = 0;

    public long curSequence = 0;

    public final LogicalTxactContext txidContext;

    private UpperJunctionMutableMetric(ZonedDateTime createTime)
    {
        super(createTime);
        this.txidContext = LogicalTxactContext.of();
    }
}

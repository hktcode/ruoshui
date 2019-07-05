/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import com.hktcode.bgsimple.triple.TripleJunctionMetric;
import com.hktcode.bgtriple.naive.NaiveJunctionMutableMetric;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalTxactContext;

import java.time.ZonedDateTime;

public class UpperJunctionMetric extends TripleJunctionMetric
{
    public static UpperJunctionMetric of(ZonedDateTime startMillis)
    {
        if (startMillis == null) {
            throw new ArgumentNullException("startMillis");
        }
        return new UpperJunctionMetric(startMillis);
    }

    public long curLsnofcmt = 0;

    public long curSequence = 0;

    public final LogicalTxactContext txidContext;

    private UpperJunctionMetric(ZonedDateTime startMillis)
    {
        super(startMillis);
        this.txidContext = LogicalTxactContext.of();
    }
}

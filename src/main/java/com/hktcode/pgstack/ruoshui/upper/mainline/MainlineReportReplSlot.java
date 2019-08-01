/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;

public class MainlineReportReplSlot
{
    static MainlineReportReplSlot of
        /* */( long totalPeriod
        /* */, long sltDuration
        /* */, PgReplSlotTuple createTuple
        /* */)
    {
        if (createTuple == null) {
            throw new ArgumentNullException("createTuple");
        }
        return new MainlineReportReplSlot(totalPeriod, sltDuration, createTuple);
    }

    public final long totalPeriod;

    public final long sltDuration;

    public final PgReplSlotTuple createTuple;

    private MainlineReportReplSlot
        /* */( long totalPeriod
        /* */, long sltDuration
        /* */, PgReplSlotTuple createTuple
        /* */)
    {
        this.totalPeriod = totalPeriod;
        this.sltDuration = sltDuration;
        this.createTuple = createTuple;
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

class MainlineMetricNormalRelaList extends MainlineMetricNormal
{
    static MainlineMetricNormalRelaList of
        /* */( long startMillis
        /* */, long actionStart
        /* */, ImmutableList<String> retryReason
        /* */)
    {
        if (retryReason == null) {
            throw new ArgumentNullException("retryReason");
        }
        return new MainlineMetricNormalRelaList
            /* */( startMillis
            /* */, actionStart
            /* */, retryReason
            /* */);
    }

    static MainlineMetricNormalRelaList of(long startMillis, long actionStart)
    {
        return new MainlineMetricNormalRelaList
            /* */( startMillis
            /* */, actionStart
            /* */, ImmutableList.of()
            /* */);
    }

    // TODO: @Deprecated
    public long getrsMillis = -1;

    // TODO: @Deprecated
    public long maxnextTime = -1;

    public final ImmutableList<String> retryReason;
    public final long actionStart;

    public final List<PgsqlRelationMetric> relationLst = new ArrayList<>();

    private MainlineMetricNormalRelaList
        /* */( long startMillis
        /* */, long actionStart
        /* */, ImmutableList<String> retryReason
        /* */)
    {
        super(startMillis);
        this.actionStart = actionStart;
        this.retryReason = retryReason;
    }
}

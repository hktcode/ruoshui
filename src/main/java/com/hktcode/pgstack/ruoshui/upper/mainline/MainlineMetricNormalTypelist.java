/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;

import java.util.ArrayList;
import java.util.List;

class MainlineMetricNormalTypelist extends MainlineMetricNormal
{
    static MainlineMetricNormalTypelist of(long startMillis, long actionStart)
    {
        return new MainlineMetricNormalTypelist(startMillis, actionStart);
    }

    // TODO: @Deprecated
    public long getrsMillis = -1;

    // TODO: @Deprecated
    public long maxnextTime = -1;

    public final long actionStart;

    private MainlineMetricNormalTypelist
        /* */( long startMillis
        /* */, long actionStart
        /* */)
    {
        super(startMillis);
        this.actionStart = actionStart;
    }
}

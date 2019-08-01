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

class MainlineMetricNormalRelaLock extends MainlineMetricNormal
{
    static MainlineMetricNormalRelaLock of
        /* */( long startMillis //
        /* */, MainlineReportRelaList getRelalist //
        /* */, long actionStart //
        /* */, ImmutableList<PgsqlRelationMetric> relationLst //
        /* */) //
    {
        if (getRelalist == null) {
            throw new ArgumentNullException("getRelalist");
        }
        if (relationLst == null) {
            throw new ArgumentNullException("relationLst");
        }
        return new MainlineMetricNormalRelaLock //
            /* */( startMillis //
            /* */, getRelalist //
            /* */, actionStart //
            /* */, relationLst //
            /* */);
    }

    public final MainlineReportRelaList getRelalist;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    public final long actionStart;

    private MainlineMetricNormalRelaLock //
        /* */( long startMillis //
        /* */, MainlineReportRelaList getRelalist //
        /* */, long actionStart //
        /* */, ImmutableList<PgsqlRelationMetric> relationLst //
        /* */) //
    {
        super(startMillis);
        this.getRelalist = getRelalist;
        this.actionStart = actionStart;
        this.relationLst = relationLst;
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

class MainlineMetricNormalSizeDiff extends MainlineMetricNormal
{
    public static MainlineMetricNormalSizeDiff of //
        /* */( long startMillis //
        /* */, MainlineReportRelaList relalist
        /* */, MainlineReportRelaLock relaLock
        /* */, MainlineReportReplSlot replSlot
        /* */, ImmutableList<PgsqlRelationMetric> oldRelalist //
        /* */, long actionStart
        /* */) //
    {
        if (relalist == null) {
            throw new ArgumentNullException("relalist");
        }
        if (relaLock == null) {
            throw new ArgumentNullException("relaLock");
        }
        if (replSlot == null) {
            throw new ArgumentNullException("replSlot");
        }
        if (oldRelalist == null) {
            throw new ArgumentNullException("oldRelalist");
        }
        return new MainlineMetricNormalSizeDiff //
            /* */( startMillis //
            /* */, relalist //
            /* */, relaLock //
            /* */, replSlot //
            /* */, oldRelalist //
            /* */, actionStart //
            /* */);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relaLock;

    public final MainlineReportReplSlot replSlot;

    public final ImmutableList<PgsqlRelationMetric> oldRelalist;

    public final List<PgsqlRelationMetric> newRelalist;

    public final long actionStart;

    private MainlineMetricNormalSizeDiff //
        /* */( long startMillis //
        /* */, MainlineReportRelaList relalist
        /* */, MainlineReportRelaLock relaLock
        /* */, MainlineReportReplSlot replSlot
        /* */, ImmutableList<PgsqlRelationMetric> oldRelalist //
        /* */, long actionStart
        /* */) //
    {
        super(startMillis);
        this.relalist = relalist;
        this.relaLock = relaLock;
        this.replSlot = replSlot;
        this.oldRelalist = oldRelalist;
        this.newRelalist = new ArrayList<>(oldRelalist.size());
        this.actionStart = actionStart;
    }
}

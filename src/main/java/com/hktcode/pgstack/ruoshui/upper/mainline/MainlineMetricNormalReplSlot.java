/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;

public class MainlineMetricNormalReplSlot extends MainlineMetricNormal
{
    static MainlineMetricNormalReplSlot of
        /* */( long startMillis
        /* */, long actionStart
        /* */, ImmutableList<PgsqlRelationMetric> relationLst
        /* */, MainlineReportRelaList relalist
        /* */, MainlineReportRelaLock relaLock
        /* */)
    {
        if (relationLst == null) {
            throw new ArgumentNullException("relationLst");
        }
        if (relalist == null) {
            throw new ArgumentNullException("relalist");
        }
        if (relaLock == null) {
            throw new ArgumentNullException("relaLock");
        }
        return new MainlineMetricNormalReplSlot //
            (startMillis, actionStart, relationLst, relalist, relaLock);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relaLock;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    public final long actionStart;

    public long sltDuration = 0;

    public PgReplSlotTuple[] createTuple = new PgReplSlotTuple[0];

    private MainlineMetricNormalReplSlot
        /* */( long startMillis
        /* */, long actionStart
        /* */, ImmutableList<PgsqlRelationMetric> relationLst
        /* */, MainlineReportRelaList relalist
        /* */, MainlineReportRelaLock relaLock
        /* */)
    {
        super(startMillis);
        this.relationLst = relationLst;
        this.actionStart = actionStart;
        this.relalist = relalist;
        this.relaLock = relaLock;
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlRelationMetric;

import java.time.ZonedDateTime;

public class MainlineMetricNormalSsBegins extends MainlineMetricNormal
{
    public static MainlineMetricNormalSsBegins of
        /* */( long startMillis
        /* */, MainlineReportRelaList relalist
        /* */, MainlineReportRelaLock relaLock
        /* */, MainlineReportReplSlot replSlot
        /* */, MainlineReportSizeDiff sizeDiff
        /* */, ImmutableList<PgsqlRelationMetric> relationLst
        /* */, long actionStart
        /* */)
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
        if (sizeDiff == null) {
            throw new ArgumentNullException("sizeDiff");
        }
        if (relationLst == null) {
            throw new ArgumentNullException("relationLst");
        }
        return new MainlineMetricNormalSsBegins //
            /* */( startMillis //
            /* */, relalist //
            /* */, relaLock //
            /* */, replSlot //
            /* */, sizeDiff //
            /* */, relationLst //
            /* */, actionStart //
            /* */);
    }

    public final MainlineReportRelaList relalist;

    public final MainlineReportRelaLock relaLock;

    public final MainlineReportReplSlot replSlot;

    public final MainlineReportSizeDiff sizeDiff;

    public final ImmutableList<PgsqlRelationMetric> relationLst;

    public final long actionStart;

    private MainlineMetricNormalSsBegins
        /* */( long startMillis
        /* */, MainlineReportRelaList relalist
        /* */, MainlineReportRelaLock relaLock
        /* */, MainlineReportReplSlot replSlot
        /* */, MainlineReportSizeDiff sizeDiff
        /* */, ImmutableList<PgsqlRelationMetric> relationLst
        /* */, long actionStart
        /* */)
    {
        super(startMillis);
        this.relalist = relalist;
        this.relaLock = relaLock;
        this.replSlot = replSlot;
        this.sizeDiff = sizeDiff;
        this.relationLst = relationLst;
        this.actionStart = actionStart;
    }
}

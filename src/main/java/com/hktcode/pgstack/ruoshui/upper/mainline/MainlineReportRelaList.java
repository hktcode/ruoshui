/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineReportRelaList implements MainlineReport
{
    public static MainlineReportRelaList of //
        /* */( long totalMillis //
        /* */, long rsgetCounts //
        /* */, long rsgetMillis //
        /* */, long rsnextCount //
        /* */, long relationLst //
        /* */, ImmutableList<String> retryReason //
        /* */) //
    {
        if (retryReason == null) {
            throw new ArgumentNullException("retryReason");
        }
        return new MainlineReportRelaList //
            /* */( totalMillis //
            /* */, rsgetCounts //
            /* */, rsgetMillis //
            /* */, rsnextCount //
            /* */, relationLst //
            /* */, retryReason //
            /* */);
    }

    static MainlineReportRelaList of(MainlineActionDataRelaList action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineReportRelaList(action, finish);
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    public final long relationLst;

    public final ImmutableList<String> retryReason;

    private MainlineReportRelaList(MainlineActionDataRelaList action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
        this.retryReason = action.retryReason;
        this.relationLst = action.relationLst.size();
    }

    private MainlineReportRelaList //
        /* */( long totalMillis //
        /* */, long rsgetCounts //
        /* */, long rsgetMillis //
        /* */, long rsnextCount //
        /* */, long relationLst //
        /* */, ImmutableList<String> retryReason //
        /* */) //
    {
        this.totalMillis = totalMillis;
        this.rsgetCounts = rsgetCounts;
        this.rsgetMillis = rsgetMillis;
        this.rsnextCount = rsnextCount;
        this.retryReason = retryReason;
        this.relationLst = relationLst;
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        node.put("total_millis", totalMillis);
        node.put("rsget_counts", rsgetCounts);
        node.put("rsget_millis", rsgetMillis);
        node.put("rsnext_count", rsnextCount);
        ArrayNode retryReasonNode = node.putArray("retry_reason");
        for (String s : this.retryReason) {
            retryReasonNode.add(s);
        }
        node.put("relation_lst", relationLst);
    }
}

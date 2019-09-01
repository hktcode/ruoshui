/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineReportTypelist implements MainlineReport
{
    static MainlineReportTypelist of(MainlineActionDataTypelist action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineReportTypelist(action, finish);
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    public final long offerCounts;

    public final long offerMillis;

    public final long recordCount;

    private MainlineReportTypelist(MainlineActionDataTypelist action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.recordCount = action.recordCount;
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        node.put("total_millis", totalMillis);
        node.put("rsget_counts", rsgetCounts);
        node.put("rsget_millis", rsgetMillis);
        node.put("rsnext_count", rsnextCount);
        node.put("offer_counts", offerCounts);
        node.put("offer_millis", offerMillis);
        node.put("record_count", recordCount);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineReportSizeDiff implements MainlineReport
{
    static MainlineReportSizeDiff of(MainlineActionDataSizeDiff action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineReportSizeDiff(action, finish);
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    public final long newRelalist;

    private MainlineReportSizeDiff(MainlineActionDataSizeDiff action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
        this.newRelalist = action.rsnextCount;
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        node.put("total_millis", totalMillis);
        node.put("rsget_counts", rsgetCounts);
        node.put("rsget_millis", rsgetMillis);
        node.put("rsnext_count", rsnextCount);
        node.put("new_relalist", newRelalist);
    }
}

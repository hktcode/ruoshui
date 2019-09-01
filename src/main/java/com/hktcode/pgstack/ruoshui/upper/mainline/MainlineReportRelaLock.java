/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineReportRelaLock implements MainlineReport
{
    static MainlineReportRelaLock of(MainlineActionDataRelaLock action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineReportRelaLock(action, finish);
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    private MainlineReportRelaLock(MainlineActionDataRelaLock action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        node.put("total_millis", totalMillis);
        node.put("rsget_counts", rsgetCounts);
        node.put("rsget_millis", rsgetMillis);
        node.put("rsnext_count", rsnextCount);
    }
}

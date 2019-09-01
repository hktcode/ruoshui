/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineReportSsBegins implements MainlineReport
{
    static MainlineReportSsBegins of(MainlineActionDataSsBegins action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineReportSsBegins(action, finish);
    }

    public final long totalMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long recordCount;

    private MainlineReportSsBegins(MainlineActionDataSsBegins action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.recordCount = action.recordCount;
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        node.put("total_millis", totalMillis);
        node.put("offer_counts", offerCounts);
        node.put("offer_millis", offerMillis);
        node.put("record_count", recordCount);
    }
}

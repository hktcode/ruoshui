/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;

public class MainlineReportReplSlot implements MainlineReport
{
    static MainlineReportReplSlot of(MainlineActionDataReplSlot action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (action.createTuple.length == 0) {
            return MainlineReportReplSlotEmpty.of(action, finish);
        }
        else {
            return MainlineReportReplSlotTuple.of(action, finish);
        }
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    public final long offerCounts;

    public final long offerMillis;

    public final long recordCount;

    public final long sltDuration;

    protected MainlineReportReplSlot(MainlineActionDataReplSlot action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.recordCount = action.recordCount;
        this.sltDuration = action.sltDuration;
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        node.put("total_millis", this.totalMillis);
        node.put("rsget_counts", this.rsgetCounts);
        node.put("rsget_millis", this.rsgetMillis);
        node.put("rsnext_count", this.rsnextCount);
        node.put("offer_counts", this.offerCounts);
        node.put("offer_millis", this.offerMillis);
        node.put("record_count", this.recordCount);
        node.put("slt_duration", this.sltDuration);
    }
}

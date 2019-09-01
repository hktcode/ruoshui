/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

public class MainlineReportTxaction implements MainlineReport
{
    static MainlineReportTxaction of(MainlineActionReplTxaction action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineReportTxaction(action, finish);
    }

    public final long totalMillis;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long recordCount;

    public final LogSequenceNumber txactionLsn;

    private MainlineReportTxaction(MainlineActionReplTxaction action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.fetchCounts = action.fetchCounts;
        this.fetchMillis = action.fetchMillis;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.recordCount = action.recordCount;
        this.txactionLsn = action.txactionLsn;
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        node.put("total_millis", totalMillis);
        node.put("fetch_counts", fetchCounts);
        node.put("fetch_millis", fetchMillis);
        node.put("offer_counts", offerCounts);
        node.put("offer_millis", offerMillis);
        node.put("record_count", recordCount);
        node.put("txaction_lsn", txactionLsn.toString());
    }
}

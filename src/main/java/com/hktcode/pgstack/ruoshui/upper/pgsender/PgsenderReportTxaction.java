/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

public class PgsenderReportTxaction
{
    static PgsenderReportTxaction of(PgsenderActionReplTxaction action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportTxaction(action, finish);
    }

    public final long totalMillis;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long offerCounts;

    public final long offerMillis;

    public final long recordCount;

    public final LogSequenceNumber txactionLsn;

    private PgsenderReportTxaction(PgsenderActionReplTxaction action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.fetchCounts = action.fetchCounts;
        this.fetchMillis = action.fetchMillis;
        this.offerCounts = action.offerCounts;
        this.offerMillis = action.offerMillis;
        this.recordCount = action.recordCount;
        this.txactionLsn = action.txactionLsn;
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineReportRelaList
{
    public static MainlineReportRelaList of //
        /* */( long totalPeriod //
        /* */, long fetchCounts //
        /* */, long fetchMillis //
        /* */, long recordCount //
        /* */, ImmutableList<String> retryReason //
        /* */) //
    {
        if (retryReason == null) {
            throw new ArgumentNullException("retryReason");
        }
        return new MainlineReportRelaList //
            /* */( totalPeriod //
            /* */, fetchCounts //
            /* */, fetchMillis //
            /* */, recordCount //
            /* */, retryReason //
            /* */);
    }

    public final long totalPeriod;

    public final long fetchCounts;

    public final long fetchMillis;

    public final long recordCount;

    public final ImmutableList<String> retryReason;

    private MainlineReportRelaList //
        /* */( long totalPeriod //
        /* */, long fetchCounts //
        /* */, long fetchMillis //
        /* */, long recordCount //
        /* */, ImmutableList<String> retryReason //
        /* */) //
    {
        this.totalPeriod = totalPeriod;
        this.fetchCounts = fetchCounts;
        this.fetchMillis = fetchMillis;
        this.recordCount = recordCount;
        this.retryReason = retryReason;
    }
}

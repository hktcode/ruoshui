/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderReportRelaList
{
    public static PgsenderReportRelaList of //
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
        return new PgsenderReportRelaList //
            /* */( totalMillis //
            /* */, rsgetCounts //
            /* */, rsgetMillis //
            /* */, rsnextCount //
            /* */, relationLst //
            /* */, retryReason //
            /* */);
    }

    public static <R, C extends PgsenderConfig<R, C>>
    PgsenderReportRelaList of(PgsenderActionDataRelaList<R, C> action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportRelaList(action, finish);
    }

    public final long totalMillis;

    public final long rsgetCounts;

    public final long rsgetMillis;

    public final long rsnextCount;

    public final long relationLst;

    public final ImmutableList<String> retryReason;

    private <R, C extends PgsenderConfig<R, C>> //
    PgsenderReportRelaList(PgsenderActionDataRelaList<R, C> action, long finish)
    {
        this.totalMillis = finish - action.actionStart;
        this.rsgetCounts = action.rsgetCounts;
        this.rsgetMillis = action.rsgetMillis;
        this.rsnextCount = action.rsnextCount;
        this.retryReason = action.retryReason;
        this.relationLst = action.relationLst.size();
    }

    private PgsenderReportRelaList //
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
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderReportReplSlot
{
    static <R, C extends PgsenderConfig<R, C>>
    PgsenderReportReplSlot of(PgsenderActionDataReplSlot<R, C> action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (action.createTuple.length == 0) {
            return PgsenderReportReplSlotEmpty.of(action, finish);
        }
        else {
            return PgsenderReportReplSlotTuple.of(action, finish);
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

    protected <R, C extends PgsenderConfig<R, C>> //
    PgsenderReportReplSlot(PgsenderActionDataReplSlot<R, C> action, long finish)
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
}

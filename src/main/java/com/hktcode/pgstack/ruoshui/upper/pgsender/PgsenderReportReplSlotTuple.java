/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;

public class PgsenderReportReplSlotTuple extends PgsenderReportReplSlot
{
    static <R, C extends PgsenderConfig>
    PgsenderReportReplSlotTuple of(PgsenderActionDataReplSlot<R, C> action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportReplSlotTuple(action, finish);
    }

    public final PgReplSlotTuple createTuple;

    private <R, C extends PgsenderConfig> //
    PgsenderReportReplSlotTuple(PgsenderActionDataReplSlot<R, C> action, long finish)
    {
        super(action, finish);
        this.createTuple = action.createTuple[0];
    }
}

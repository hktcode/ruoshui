/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;

public class PgsenderReportReplSlotTuple extends PgsenderReportReplSlot
{
    static PgsenderReportReplSlotTuple of(PgsenderActionDataReplSlot action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportReplSlotTuple(action, finish);
    }

    public final PgReplSlotTuple createTuple;

    private PgsenderReportReplSlotTuple(PgsenderActionDataReplSlot action, long finish)
    {
        super(action, finish);
        this.createTuple = action.createTuple[0];
    }
}

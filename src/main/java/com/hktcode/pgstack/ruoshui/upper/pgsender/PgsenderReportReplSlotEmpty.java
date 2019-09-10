/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderReportReplSlotEmpty extends PgsenderReportReplSlot
{
    static <R, C extends PgsenderConfig> //
    PgsenderReportReplSlotEmpty of(PgsenderActionDataReplSlot<R, C> action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderReportReplSlotEmpty(action, finish);
    }

    private <R, C extends PgsenderConfig> //
    PgsenderReportReplSlotEmpty(PgsenderActionDataReplSlot<R, C> action, long finish)
    {
        super(action, finish);
    }
}

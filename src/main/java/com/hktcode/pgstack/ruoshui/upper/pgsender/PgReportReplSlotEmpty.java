/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgReportReplSlotEmpty extends PgReportReplSlot
{
    static PgReportReplSlotEmpty of(PgActionDataReplSlot action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgReportReplSlotEmpty(action, finish);
    }

    private PgReportReplSlotEmpty(PgActionDataReplSlot action, long finish)
    {
        super(action, finish);
    }
}

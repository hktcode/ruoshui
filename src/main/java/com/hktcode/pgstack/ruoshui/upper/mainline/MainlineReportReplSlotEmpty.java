/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;

public class MainlineReportReplSlotEmpty extends MainlineReportReplSlot
{
    static MainlineReportReplSlotEmpty of(MainlineActionDataReplSlot action, long finish)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineReportReplSlotEmpty(action, finish);
    }

    private MainlineReportReplSlotEmpty(MainlineActionDataReplSlot action, long finish)
    {
        super(action, finish);
    }
}

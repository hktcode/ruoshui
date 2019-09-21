/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricRunReplSlotStraight implements PgMetricRun
{
    static PgMetricRunReplSlotStraight of(PgActionDataReplSlotStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricRunReplSlotStraight(action);
    }

    private PgMetricRunReplSlotStraight(PgActionDataReplSlotStraight action)
    {
        long finish = System.currentTimeMillis();
        this.replslot = PgReportReplSlot.of(action, finish);
    }

    public final PgReportReplSlot replslot;
}

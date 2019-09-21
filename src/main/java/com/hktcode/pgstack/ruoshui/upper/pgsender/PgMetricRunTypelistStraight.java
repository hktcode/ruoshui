/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricRunTypelistStraight extends PgMetricRunTypelist
{
    static PgMetricRunTypelistStraight of(PgActionDataTypelistStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricRunTypelistStraight(action);
    }

    private PgMetricRunTypelistStraight(PgActionDataTypelistStraight action)
    {
        super(action);
        this.replslot = action.replslot;
    }

    public final PgReportReplSlot replslot;
}

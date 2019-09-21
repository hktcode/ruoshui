/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricRunTxactionStraight extends PgMetricRunTxaction
{
    static PgMetricRunTxactionStraight of(PgActionReplTxactionStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricRunTxactionStraight(action);
    }

    private PgMetricRunTxactionStraight(PgActionReplTxactionStraight action)
    {
        super(action);
        this.replslot = action.replslot;
    }

    public final PgReportReplSlot replslot;
}

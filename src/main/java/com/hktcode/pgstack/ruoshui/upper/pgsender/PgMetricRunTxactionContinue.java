/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricRunTxactionContinue extends PgMetricRunTxaction
{
    static PgMetricRunTxactionContinue of(PgActionReplTxactionContinue action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricRunTxactionContinue(action);
    }

    private PgMetricRunTxactionContinue(PgActionReplTxactionContinue action)
    {
        super(action);
        this.relalist = action.relalist;
        this.relalock = action.relaLock;
        this.replslot = action.replSlot;
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relalock;

    public final PgReportReplSlot replslot;
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

class PgActionReplTxactionContinue extends PgActionReplTxaction
{
    static PgActionReplTxactionContinue of(PgActionDataTypelistContinue action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionReplTxactionContinue(action);
    }

    final PgReportRelaList relalist;

    final PgReportRelaLock relaLock;

    final PgReportReplSlot replSlot;

    private PgActionReplTxactionContinue(PgActionDataTypelistContinue action)
    {
        super(action);
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = action.replSlot;
    }

    @Override
    public PgMetricRunTxactionContinue toRunMetrics()
    {
        return PgMetricRunTxactionContinue.of(this);
    }
}


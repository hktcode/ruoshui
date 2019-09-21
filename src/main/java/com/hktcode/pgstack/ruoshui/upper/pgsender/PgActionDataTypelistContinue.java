/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

class PgActionDataTypelistContinue extends PgActionDataTypelist
{
    static PgActionDataTypelistContinue of(PgActionDataReplSlotSnapshot action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataTypelistContinue(action);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relaLock;

    public final PgReportReplSlot replSlot;

    private PgActionDataTypelistContinue(PgActionDataReplSlotSnapshot action)
    {
        super(action);
        this.relalist = action.relalist;
        this.relaLock = action.relaLock;
        this.replSlot = PgReportReplSlotEmpty.of(action, this.actionStart);
    }

    public PgActionReplTxaction txaction()
    {
        return PgActionReplTxactionContinue.of(this);
    }

    @Override
    public PgMetricRunTypelistContinue toRunMetrics()
    {
        return PgMetricRunTypelistContinue.of(this);
    }
}


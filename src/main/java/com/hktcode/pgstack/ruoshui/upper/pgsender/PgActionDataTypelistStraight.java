/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

class PgActionDataTypelistStraight extends PgActionDataTypelist
{
    static PgActionDataTypelistStraight of(PgActionDataReplSlotStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataTypelistStraight(action);
    }

    final PgReportReplSlot replslot;

    private PgActionDataTypelistStraight(PgActionDataReplSlotStraight action)
    {
        super((PgConfigMainline) action.config, action.status, action.tqueue);
        this.replslot = PgReportReplSlot.of(action, this.actionStart);
    }

    public PgActionReplTxactionStraight txaction()
    {
        return PgActionReplTxactionStraight.of(this);
    }

    @Override
    public PgMetricRunTypelistStraight toRunMetrics()
    {
        return PgMetricRunTypelistStraight.of(this);
    }
}


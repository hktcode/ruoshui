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
    static PgActionDataTypelistStraight of
        /* */(PgConfigMainline config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        return new PgActionDataTypelistStraight(config, status, tqueue);
    }

    private PgActionDataTypelistStraight
        /* */(PgConfigMainline config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */)
    {
        super(config, status, tqueue);
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

    @Override
    public PgMetricEndTypelistStraight toEndMetrics()
    {
        return PgMetricEndTypelistStraight.of(this);
    }
}


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
        /* */( MainlineConfig config //
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
        /* */( MainlineConfig config //
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
    public PgsenderMetricRunTypelistStraight toRunMetrics()
    {
        return PgsenderMetricRunTypelistStraight.of(this);
    }

    @Override
    public PgsenderMetricEndTypelistStraight toEndMetrics()
    {
        return PgsenderMetricEndTypelistStraight.of(this);
    }
}


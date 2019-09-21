/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNegativeException;
import com.hktcode.lang.exception.ArgumentNullException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

class PgActionDataReplSlotStraight extends PgActionDataReplSlot
{
    static PgActionDataReplSlotStraight of
        /* */( PgConfigMainline config //
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
        return new PgActionDataReplSlotStraight(config, status, tqueue);
    }

    private PgActionDataReplSlotStraight
        /* */( PgConfigMainline config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */)
    {
        super(config, status, tqueue);
    }

    @Override
    PgAction complete(Connection pgdata) throws SQLException
    {
        return PgActionDataTypelistStraight.of(this);
    }

    @Override
    public PgMetricRunReplSlotStraight toRunMetrics()
    {
        return PgMetricRunReplSlotStraight.of(this);
    }
}

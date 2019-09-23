/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

abstract class PgActionDataReplSlot extends PgActionDataQuerySql
{
    private static final Logger logger = LoggerFactory.getLogger(PgActionDataReplSlot.class);

    long sltDuration = 0;

    PgReplSlotTuple[] createTuple = new PgReplSlotTuple[0];

    PgActionDataReplSlot
        /* */( PgConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */)
    {
        super(config, status, tqueue);
    }

    PgActionDataReplSlot(PgActionDataRelaLock action)
    {
        super(action, System.currentTimeMillis());
        this.logDatetime = action.logDatetime;
    }

    @Override
    PgRecord build(ResultSet rs) throws SQLException
    {
        if (this.createTuple.length != 0) {
            // the CREATE REPLICATION SLOT statement only has one tuple
            throw new RuntimeException(); // TODO:
        }
        PgReplSlotTuple tuple = PgReplSlotTuple.of(rs);
        logger.info("create slot success: tuple={}", tuple);
        this.createTuple = new PgReplSlotTuple[] { tuple };
        return PgRecordCreateSlot.of(tuple);
    }

    @Override
    PgDeputeReplSlot createDepute(PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException
    {
        return this.config.newCreateSlot(pgrepl.createStatement());
    }
}

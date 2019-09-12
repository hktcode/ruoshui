/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

class PgActionDataReplSlot extends PgActionDataQuerySql
{
    private static final Logger logger = LoggerFactory.getLogger(PgActionDataReplSlot.class);

    static PgActionDataReplSlot of(PgActionDataRelaLock action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionDataReplSlot(action);
    }

    public final PgReportRelaList relalist;

    public final PgReportRelaLock relaLock;

    public final ImmutableList<PgStructRelainfo> relationLst;

    long sltDuration = 0;

    PgReplSlotTuple[] createTuple = new PgReplSlotTuple[0];

    private PgActionDataReplSlot(PgActionDataRelaLock action)
    {
        super(action, System.currentTimeMillis());
        this.relalist = action.relalist;
        this.relaLock = PgReportRelaLock.of(action, this.actionStart);
        this.relationLst = action.relationLst;
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
        return this.config.createSlotMsg(tuple);
    }

    @Override
    PgRecord initRecord()
    {
        return this.config.pauseWorldMsg();
    }

    @Override
    PgAction complete(Connection pgdata)
    {
        if (this.createTuple.length == 0) {
            throw new RuntimeException(); // TODO:
        }
        return PgActionDataSizeDiff.of(this);
    }

    @Override
    PgDeputeCreateSlot createDepute(PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException
    {
        return this.config.newCreateSlot(pgrepl.createStatement());
    }

    @Override
    public PgMetricRunReplSlot toRunMetrics()
    {
        return PgMetricRunReplSlot.of(this);
    }
}

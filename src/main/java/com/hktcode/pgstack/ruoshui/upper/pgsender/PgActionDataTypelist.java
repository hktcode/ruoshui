/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.pgjdbc.LogicalDatatypeInfMsg;
import org.postgresql.jdbc.PgConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

abstract class PgActionDataTypelist extends PgActionDataQuerySql
{
    PgActionDataTypelist
        /* */(PgConfigMainline config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */)
    {
        super(config, status, tqueue);
        this.logDatetime = super.actionStart;
    }

    PgActionDataTypelist(PgActionDataSsFinish action)
    {
        super(action, System.currentTimeMillis());
        this.logDatetime = action.logDatetime;
    }

    public abstract PgActionReplTxaction txaction();

    @Override
    PgRecord build(ResultSet rs) //
        throws SQLException
    {
        long d = rs.getLong("datatype");
        String p = rs.getString("tpschema");
        String n = rs.getString("typename");
        LogicalDatatypeInfMsg m = LogicalDatatypeInfMsg.of(d, p, n);
        return PgRecordLogicalMsg.of(0L, m);
    }

    @Override
    PgAction complete(Connection pgdata) throws SQLException
    {
        pgdata.commit();
        this.statusInfor = "send txation finish record, txaction starts.";
        return this.txaction();
    }

    @Override
    PgDeputeSelectData createDepute(PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException
    {
        return PgDeputeSelectData.of(this.config.queryTypelist(pgdata));
    }
}


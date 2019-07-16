/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatusInnerEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalDatatypeInfMsg;
import com.hktcode.pgjdbc.PostgreSQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;

class PgsqlTxactionMetricDatatype extends PgsqlTxactionMetric
{
    static PgsqlTxactionMetricDatatype of(MainlineConfig config, ZonedDateTime startMillis)
    {
        if (startMillis == null) {
            throw new ArgumentNullException("startMillis");
        }
        return new PgsqlTxactionMetricDatatype(config, startMillis);
    }

    private final MainlineConfig config;

    private PgsqlTxactionMetricDatatype(MainlineConfig config, ZonedDateTime startMillis)
    {
        super(startMillis);
        this.config = config;
    }

    PgsqlTxactionMetric next(PgsqlTxaction worker) throws SQLException, InterruptedException
    {
        PgsqlTxactionRecord r = null;
        super.statusInfor = "query data types";
        final int fetchSize = 128;
        try (Connection c = config.srcProperty.queriesConnection();
             Statement s = PostgreSQL.createStatement(c, fetchSize);
             ResultSet rs = s.executeQuery(TYPES_SELECT)) {
            rs.setFetchDirection(ResultSet.FETCH_FORWARD);
            rs.setFetchSize(fetchSize);
            // TODO: s.executeQuery 和 rs.next 可能导致长时间无响应.
            while (!(worker.newStatus(worker) instanceof SimpleStatusInnerEnd)) {
                long timeout = config.logDuration;
                if (r != null) {
                    r = worker.push(r, timeout, timeout, this);
                } else if (rs.next()) {
                    long d = rs.getLong("datatype");
                    String p = rs.getString("tpschema");
                    String n = rs.getString("typename");
                    LogicalDatatypeInfMsg m = LogicalDatatypeInfMsg.of(d, p, n);
                    r = PgsqlTxactionRecordNormal.of(0L, m);
                } else if (config instanceof MainlineConfigSnapshot) {
                    this.statusInfor = "send txation finish record, snapshot starts.";
                    return PgsqlTxactionMetricNormalSnapshot.of((MainlineConfigSnapshot) config, this);
                }
                else {
                    this.statusInfor = "send txation finish record, txaction starts.";
                    return PgsqlTxactionMetricNormalTxaction.of(config, this);
                }
            }
            this.statusInfor = "upper consumer end when query data types";
            ZonedDateTime attime = ZonedDateTime.now();
            return PgsqlTxactionMetricFinish.of(config, this, attime);
        }
    }

    private static final String TYPES_SELECT = "" //
        + "\n select \"t\".    \"oid\"::int8 as \"datatype\" " //
        + "\n      , \"n\".\"nspname\"::text as \"tpschema\" " //
        + "\n      , \"t\".\"typname\"::text as \"typename\" " //
        + "\n  from            \"pg_catalog\".\"pg_type\"      \"t\" " //
        + "\n       inner join \"pg_catalog\".\"pg_namespace\" \"n\" " //
        + "\n               on \"t\".\"typnamespace\" = \"n\".\"oid\" " //
        + "\n ";
}

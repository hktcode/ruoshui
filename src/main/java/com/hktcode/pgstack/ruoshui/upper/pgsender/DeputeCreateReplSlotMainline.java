/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import org.postgresql.core.Utils;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.Callable;

public class DeputeCreateReplSlotMainline implements Callable<PgReplSlotTuple>
{
    private static final Logger logger = LoggerFactory.getLogger(DeputeCreateReplSlotMainline.class);

    public static DeputeCreateReplSlotMainline of(Statement statement, String sqlscript)
    {
        if (statement == null) {
            throw new ArgumentNullException("statement");
        }
        if (sqlscript == null) {
            throw new ArgumentNullException("sqlscript");
        }
        return new DeputeCreateReplSlotMainline(statement, sqlscript);
    }

    private final Statement statement;

    private final String slotname;

    private DeputeCreateReplSlotMainline(Statement statement, String slotname)
    {
        this.statement = statement;
        this.slotname = slotname;
    }

    private String buildCreateSlotStatement() throws SQLException
    {
        StringBuilder sb = new StringBuilder("CREATE_REPLICATION_SLOT ");
        Utils.escapeIdentifier(sb, slotname);
        sb.append(" LOGICAL pgoutput EXPORT_SNAPSHOT");
        return sb.toString();
    }

    @Override
    public PgReplSlotTuple call() throws SQLException
    {
        long starts = System.currentTimeMillis();
        String sqlscript = this.buildCreateSlotStatement();
        try (ResultSet rs = statement.executeQuery(sqlscript)) {
            long finish = System.currentTimeMillis();
            logger.info("create slot execute query: duration={}", finish - starts);
            starts = System.currentTimeMillis();
            if (!rs.next()) {
                // the CREATE REPLICATION SLOT statement only has one tuple
                throw new RuntimeException(); // TODO:
            }
            finish = System.currentTimeMillis();
            logger.info("create slot result set next: duration={}", finish - starts);
            // NOTE:XXX: 此处需要处理null问题么？需要处理rs返回多条记录的问题么？
            String slotName = rs.getString("slot_name");
            String snapshotName = rs.getString("snapshot_name");
            String outputPlugin = rs.getString("output_plugin");
            String consistentPointTxt = rs.getString("consistent_point");
            LogSequenceNumber consistentPoint = LogSequenceNumber.INVALID_LSN;
            if (consistentPointTxt != null) {
                consistentPoint = LogSequenceNumber.valueOf(consistentPointTxt);
            }
            if (rs.next()) {
                throw new RuntimeException(); // TODO:
            }
            return PgReplSlotTuple.of //
                /* */( slotName //
                /* */, consistentPoint.asLong() //
                /* */, Objects.toString(snapshotName, "") //
                /* */, Objects.toString(outputPlugin, "") //
                /* */);
        }
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.core.Utils;
import org.postgresql.util.PSQLException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class PgDeputeCreateSlotSnapshot extends PgDeputeCreateSlot
{
    static PgDeputeCreateSlotSnapshot of(Statement statement, String nameinfor)
    {
        if (statement == null) {
            throw new ArgumentNullException("statement");
        }
        if (nameinfor == null) {
            throw new ArgumentNullException("nameinfor");
        }
        return new PgDeputeCreateSlotSnapshot(statement, nameinfor);
    }

    private PgDeputeCreateSlotSnapshot(Statement statement, String nameinfor)
    {
        super(statement, nameinfor);
    }

    @Override
    public ResultSet call() throws SQLException
    {
        long count = (long)(Long.MAX_VALUE * Math.random());
        String prefix = String.format("%s_tmp_", this.nameinfor);
        final long SLOT_NAME_MAX_LENGTH = 63;
        long countLength = SLOT_NAME_MAX_LENGTH - prefix.length();
        if (countLength > 16) {
            countLength = 16;
        }
        final String format = "%s%0" + countLength + "x";
        do {
            ++count;
            String name = String.format(format, prefix, count);
            String sqlscript = this.buildCreateSlotStatement(name);
            try {
                return statement.executeQuery(sqlscript);
            }
            catch (PSQLException ex) {
                String sqlState = ex.getSQLState();
                if ("42710".equals(sqlState)) {
                    // 复制槽已经存在，重试.
                    continue;
                }
                throw ex;
            }
        } while (true);
    }

    private String buildCreateSlotStatement(String slotname) //
        throws SQLException
    {
        StringBuilder sb = new StringBuilder("CREATE_REPLICATION_SLOT ");
        Utils.escapeIdentifier(sb, slotname);
        sb.append(" TEMPORARY LOGICAL pgoutput EXPORT_SNAPSHOT");
        return sb.toString();
    }
}

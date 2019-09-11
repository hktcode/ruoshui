/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.core.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class PgDeputeCreateSlotMainline extends PgDeputeCreateSlot
{
    static PgDeputeCreateSlotMainline of(Statement statement, String nameinfor)
    {
        if (statement == null) {
            throw new ArgumentNullException("statement");
        }
        if (nameinfor == null) {
            throw new ArgumentNullException("nameinfor");
        }
        return new PgDeputeCreateSlotMainline(statement, nameinfor);
    }

    private PgDeputeCreateSlotMainline(Statement statement, String nameinfor)
    {
        super(statement, nameinfor);
    }

    @Override
    public ResultSet call() throws SQLException
    {
        String sqlScript = this.buildCreateSlotStatement();
        return this.statement.executeQuery(sqlScript);
    }

    private String buildCreateSlotStatement() throws SQLException
    {
        StringBuilder sb = new StringBuilder("CREATE_REPLICATION_SLOT ");
        Utils.escapeIdentifier(sb, nameinfor);
        sb.append(" LOGICAL pgoutput EXPORT_SNAPSHOT");
        return sb.toString();
    }
}

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

class PgDeputeCreateSlotMainline extends PgDeputeCreateSlot
{
    static PgDeputeCreateSlotMainline of(Statement statement, String nameinfor, boolean snapshots)
    {
        if (statement == null) {
            throw new ArgumentNullException("statement");
        }
        if (nameinfor == null) {
            throw new ArgumentNullException("nameinfor");
        }
        return new PgDeputeCreateSlotMainline(statement, nameinfor, snapshots);
    }

    private static final String EMPTY_SELECT = "" //
        + "\n select         ''::text as slot_name " //
        + "\n      ,         ''::text as consistent_point " //
        + "\n      ,         ''::text as snapshot_name " //
        + "\n      , 'pgoutput'::text as output_plugin " //
        + "\n   from (select 1) t " //
        + "\n  where 1 = 0 " //
        ;

    private final boolean snapshots;

    private PgDeputeCreateSlotMainline(Statement statement, String nameinfor, boolean snapshots)
    {
        super(statement, nameinfor);
        this.snapshots = snapshots;
    }

    @Override
    public ResultSet call() throws SQLException
    {
        String sqlScript = this.buildCreateSlotStatement();
        try {
            return this.statement.executeQuery(sqlScript);
        }
        catch (PSQLException ex) {
            if ("42710".equals(ex.getSQLState())) {
                return this.statement.executeQuery(EMPTY_SELECT);
            }
            throw ex;
        }
    }

    private String buildCreateSlotStatement() throws SQLException
    {
        StringBuilder sb = new StringBuilder("CREATE_REPLICATION_SLOT ");
        Utils.escapeIdentifier(sb, nameinfor);
        sb.append(" LOGICAL pgoutput");
        if (snapshots) {
            sb.append(" EXPORT_SNAPSHOT ");
        }
        else {
            sb.append(" NOEXPORT_SNAPSHOT ");
        }
        return sb.toString();
    }
}

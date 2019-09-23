/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import java.sql.SQLException;
import java.sql.Statement;

abstract class PgDeputeReplSlot implements PgDeputeStatement
{
    final Statement statement;

    final String nameinfor;

    PgDeputeReplSlot(Statement statement, String nameinfor)
    {
        this.statement = statement;
        this.nameinfor = nameinfor;
    }

    @Override
    public void close() throws SQLException
    {
        this.statement.close();
    }
}

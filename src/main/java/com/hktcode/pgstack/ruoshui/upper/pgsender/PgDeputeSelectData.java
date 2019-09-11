/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PgDeputeSelectData implements PgDeputeStatement
{
    public static PgDeputeSelectData of(PreparedStatement statement)
    {
        if (statement == null) {
            throw new ArgumentNullException("statement");
        }
        return new PgDeputeSelectData(statement);
    }

    protected PreparedStatement statement;

    private PgDeputeSelectData(PreparedStatement statement)
    {
        this.statement = statement;
    }

    @Override
    public ResultSet call() throws SQLException
    {
        return this.statement.executeQuery();
    }

    @Override
    public void close() throws SQLException
    {
        this.statement.close();
    }
}

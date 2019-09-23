/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class PgDeputeResultSetNext implements Callable<Boolean>
{
    public static PgDeputeResultSetNext of(ResultSet resultSet)
    {
        if (resultSet == null) {
            throw new ArgumentNullException("resultSet");
        }
        return new PgDeputeResultSetNext(resultSet);
    }

    private final ResultSet resultSet;

    private PgDeputeResultSetNext(ResultSet resultSet)
    {
        this.resultSet = resultSet;
    }

    @Override
    public Boolean call() throws SQLException
    {
        return this.resultSet.next();
    }
}

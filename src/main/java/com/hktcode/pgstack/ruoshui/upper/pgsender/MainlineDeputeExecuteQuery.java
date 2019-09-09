/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class MainlineDeputeExecuteQuery implements Callable<ResultSet>
{
    public static MainlineDeputeExecuteQuery of(PreparedStatement statement)
    {
        if (statement == null) {
            throw new ArgumentNullException("statement");
        }
        return new MainlineDeputeExecuteQuery(statement);
    }

    private final PreparedStatement statement;

    private MainlineDeputeExecuteQuery(PreparedStatement statement)
    {
        this.statement = statement;
    }

    @Override
    public ResultSet call() throws SQLException
    {
        return statement.executeQuery();
    }
}

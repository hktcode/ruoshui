/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class MainlineDeputeResultSetNext implements Callable<Boolean>
{
    public static MainlineDeputeResultSetNext of(ResultSet resultSet)
    {
        if (resultSet == null) {
            throw new ArgumentNullException("resultSet");
        }
        return new MainlineDeputeResultSetNext(resultSet);
    }

    private final ResultSet resultSet;

    private MainlineDeputeResultSetNext(ResultSet resultSet)
    {
        this.resultSet = resultSet;
    }

    @Override
    public Boolean call() throws SQLException
    {
        return this.resultSet.next();
    }
}

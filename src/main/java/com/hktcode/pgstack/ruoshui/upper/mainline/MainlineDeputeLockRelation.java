/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.util.PSQLException;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

public class MainlineDeputeLockRelation implements Callable<Boolean>
{
    public static MainlineDeputeLockRelation of(Statement statement, String sqlscript)
    {
        if (statement == null) {
            throw new ArgumentNullException("statement");
        }
        if (sqlscript == null) {
            throw new ArgumentNullException("sqlscript");
        }
        return new MainlineDeputeLockRelation(statement, sqlscript);
    }

    private final Statement statement;

    private final String sqlscript;

    private MainlineDeputeLockRelation(Statement statement, String sqlscript)
    {
        this.statement = statement;
        this.sqlscript = sqlscript;
    }

    @Override
    public Boolean call() throws SQLException
    {
        try {
            return this.statement.execute(sqlscript);
        }
        catch (PSQLException ex) {
            String sqlstate = ex.getSQLState();
            if ("42P01".equals(sqlstate)) {
                return false;
            }
            throw ex;
        }
    }
}

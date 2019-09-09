/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

public class DeputeLockRelationMainline implements Callable<Boolean>
{
    public static DeputeLockRelationMainline of(Statement statement, String sqlscript)
    {
        if (statement == null) {
            throw new ArgumentNullException("statement");
        }
        if (sqlscript == null) {
            throw new ArgumentNullException("sqlscript");
        }
        return new DeputeLockRelationMainline(statement, sqlscript);
    }

    private final Statement statement;

    private final String sqlscript;

    private DeputeLockRelationMainline(Statement statement, String sqlscript)
    {
        this.statement = statement;
        this.sqlscript = sqlscript;
    }

    private static final Logger logger = LoggerFactory.getLogger(DeputeLockRelationMainline.class);

    @Override
    public Boolean call() throws SQLException
    {
        try {
            // TODO: 为什么这里总是返回false.
            boolean success = this.statement.execute(sqlscript);
            logger.info("execute sql: success={}, sqlscript={}", success, sqlscript);
            return true;
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

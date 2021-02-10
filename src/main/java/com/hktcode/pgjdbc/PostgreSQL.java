/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.hktcode.lang.exception.ArgumentNullException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

/**
 * the util class of PostgreSQL.
 */
public class PostgreSQL
{
    /**
     * the jdbc url prefix.
     */
    public static final String JDBC_URL = "jdbc:postgresql:";

    /**
     * replication slot name's length must be less than 64. and may only contain
     * lower case letters, numbers and the underscore character,
     */
    public static final Pattern slotNamePattern = Pattern.compile("[0-9a-z_]{1,63}");

    /**
     * PostgreSQL epoch(2000-01-01).
     */
    public static final ZonedDateTime EPOCH = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("GMT"));

    /**
     * Obtain a ZonedDateTime from a PostgreSQL microsencond.
     *
     * @param postgresMicros a PostgreSQL microsencond.
     * @return a ZonedDateTime object.
     */
    public static ZonedDateTime toZonedDatetime(long postgresMicros)
    {
        return PostgreSQL.EPOCH.plus(postgresMicros, ChronoUnit.MICROS);
    }

    /**
     * constructor function.
     *
     * just for closing SonarLint warning.
     */
    private PostgreSQL()
    {
    }

    /**
     * Create a read only, forward only and close cursors at commit statement.
     *
     * @param connection the connection of PostgreSQL.
     * @param fetchSize default fetch size.
     * @return a Statement Object.
     * @throws SQLException if create Statement or set fetch info error.
     */
    public static Statement createStatement(Connection connection, int fetchSize)
        throws SQLException
    {
        if (connection == null) {
            throw new ArgumentNullException("connection");
        }
        Statement s = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY //
            , ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
        try {
            setStatement(s, fetchSize);
            return s;
        }
        catch (Exception ex) {
            s.close();
            throw ex;
        }
    }

    /**
     * set statement with fetch forward and fetch size.
     *
     * @param s statement which will be setted.
     * @param fetchSize default fetch size.
     * @throws SQLException if set error.
     */
    private static void setStatement(Statement s, int fetchSize)
        throws SQLException
    {
        s.setFetchDirection(ResultSet.FETCH_FORWARD);
        s.setFetchSize(fetchSize);
    }
}

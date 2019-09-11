/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

interface PgDeputeStatement extends Callable<ResultSet>, AutoCloseable
{
    @Override
    void close() throws SQLException;
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;
import java.time.ZonedDateTime;

abstract class PgsqlTxactionMetricNormal extends PgsqlTxactionMetric
{
    PgsqlTxactionMetricNormal(ZonedDateTime startMillis)
    {
        super(startMillis);
    }

    abstract PgsqlTxactionMetric next(PgConnection pgrepl, PgsqlTxaction worker) //
        throws SQLException, InterruptedException;
}

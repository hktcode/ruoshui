/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PostgreSQL;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperRunnableMetric;
import org.postgresql.jdbc.PgConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;

public abstract class PgsqlTxactionMetric extends UpperRunnableMetric
{
    // public final ZonedDateTime startMillis;

    // public long recordCount = 0;

    // public long fetchCounts = 0;

    // public long fetchMillis = 0;

    // public long offerCounts = 0;

    // public long offerMillis = 0;

    // public long logDatetime = 0;

    // public String statusInfor = "";

    protected PgsqlTxactionMetric(ZonedDateTime startMillis)
    {
        super(startMillis);
        // this.startMillis = startMillis;
    }
}

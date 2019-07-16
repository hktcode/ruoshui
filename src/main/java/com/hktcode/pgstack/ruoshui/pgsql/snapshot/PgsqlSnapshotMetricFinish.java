/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;

public interface PgsqlSnapshotMetricFinish extends PgsqlSnapshotMetric
{
    // public final PgsqlSnapshotMetric metric;

    // protected PgsqlSnapshotMetricFinish(PgsqlSnapshotMetric metric)
    // {
    //     this.metric = metric;
    // }

    boolean finish(Connection pgdata) throws SQLException;
}

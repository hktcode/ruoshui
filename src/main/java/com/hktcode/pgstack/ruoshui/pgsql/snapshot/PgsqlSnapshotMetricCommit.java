/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 获取快照过程正常结束.
 */
public interface PgsqlSnapshotMetricCommit extends PgsqlSnapshotMetricFinish
{
    // /**
    //  * Obtain a {@link PgsqlSnapshotMetricCommit} Object.
    //  *
    //  * @return a {@link PgsqlSnapshotMetricCommit} Object.
    //  */
    // public static PgsqlSnapshotMetricCommit of(PgsqlSnapshotMetric metric)
    // {
    //     return new PgsqlSnapshotMetricCommit(metric);
    // }

    // /**
    //  * constructor.
    //  */
    // private PgsqlSnapshotMetricCommit(PgsqlSnapshotMetric metric)
    // {
    //     super(metric);
    // }

    @Override
    default boolean finish(Connection pgdata) throws SQLException
    {
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        pgdata.commit();
        return true;
    }
}

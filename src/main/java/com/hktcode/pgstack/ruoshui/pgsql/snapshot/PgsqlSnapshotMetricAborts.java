/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 获取快照过程异常结束.
 *
 * @param <M> 快照的metric类.
 */
public interface PgsqlSnapshotMetricAborts extends PgsqlSnapshotMetricFinish
{
    // protected PgsqlSnapshotMetricAborts(PgsqlSnapshotMetricProcess metric)
    // {
    //     super(metric);
    // }

    default boolean finish(Connection pgdata) throws SQLException
    {
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        pgdata.rollback();
        return false;
    }
}

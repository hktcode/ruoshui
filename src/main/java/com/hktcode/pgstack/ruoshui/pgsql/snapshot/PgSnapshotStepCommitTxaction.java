/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 获取快照过程正常结束.
 *
 * @param <M> 快照的metric类.
 */
public class PgSnapshotStepCommitTxaction<M> implements PgSnapshotStepFinish<M>
{
    /**
     * Obtain a {@link PgSnapshotStepCommitTxaction} Object.
     *
     * @param <M> the class of snapshot metric.
     *
     * @return a {@link PgSnapshotStepCommitTxaction} Object.
     */
    public static<M> PgSnapshotStepCommitTxaction<M> of()
    {
        return new PgSnapshotStepCommitTxaction<>();
    }

    /**
     * constructor.
     */
    private PgSnapshotStepCommitTxaction()
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean finish(Connection pgdata) throws SQLException
    {
        if (pgdata == null) {
            throw new ArgumentNullException("pgdata");
        }
        pgdata.commit();
        return true;
    }
}

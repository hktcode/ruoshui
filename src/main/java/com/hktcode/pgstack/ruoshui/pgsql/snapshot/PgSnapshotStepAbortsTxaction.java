/*
 * Copyright (c) 2019, Huang Ketian
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
public class PgSnapshotStepAbortsTxaction<M> implements PgSnapshotStepFinish<M>
{
    /**
     * Obtain a {@link PgSnapshotStepAbortsTxaction} Object.
     *
     * @param <M> the class of snapshot metric.
     *
     * @return a {@link PgSnapshotStepAbortsTxaction} Object.
     */
    public static<M> PgSnapshotStepAbortsTxaction<M> of()
    {
        return new PgSnapshotStepAbortsTxaction<>();
    }

    /**
     * constructor.
     */
    private PgSnapshotStepAbortsTxaction()
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
        pgdata.rollback();
        return false;
    }
}

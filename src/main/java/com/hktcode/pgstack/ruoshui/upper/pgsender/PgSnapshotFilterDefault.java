/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplRelation;

/**
 * 默认快照过滤器，对所有关系都返回{@code true}，不过滤任何关系.
 */
public class PgSnapshotFilterDefault implements PgSnapshotFilter
{
    /**
     * Obtain a {@link PgSnapshotFilterDefault} Object.
     *
     * @return a {@link PgSnapshotFilterDefault} Object.
     */
    public static PgSnapshotFilterDefault of()
    {
        return new PgSnapshotFilterDefault();
    }

    /**
     * constructor.
     */
    private PgSnapshotFilterDefault()
    {
    }

    /**
     * {@inheritDoc}
     *
     * always return true.
     */
    @Override
    public boolean eval(PgReplRelation relation)
    {
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return true;
   }
}
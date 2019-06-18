/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 关系快照的tuple消息.
 *
 * 当前PostgreSQL流复制协议中并不会产生这个消息。
 * 这个消息是本程序在获取快照时发出的。
 */
public class LogicalCreateTupleMsg implements LogicalMsg
{
    /**
     * Obtain a {@link LogicalCreateTupleMsg} from a relation and tupleval.
     *
     * @param relation the relation of tuple.
     * @param tupleval the tuple value.
     *
     * @return a {@link LogicalCreateTupleMsg} Object
     * @throws ArgumentNullException if {@code relation} or {@code tupleval} is {@code null}
     */
    public static LogicalCreateTupleMsg of //
        /* */( PgReplRelation relation //
        /* */, ImmutableList<PgReplComponent> tupleval //
        /* */)
    {
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        if (tupleval == null) {
            throw new ArgumentNullException("tupleval");
        }
        return new LogicalCreateTupleMsg(relation, tupleval);
    }

    /**
     * the relation of tuple.
     */
    public final PgReplRelation relation;

    /**
     * the tuple value.
     */
    public final ImmutableList<PgReplComponent> tupleval;

    /**
     * Costructor function.
     *
     * @param relation the relation of tuple.
     * @param tupleval the tuple value.
     */
    private LogicalCreateTupleMsg //
        /* */( PgReplRelation relation //
        /* */, ImmutableList<PgReplComponent> tupleval //
        /* */)
    {
        this.relation = relation;
        this.tupleval = tupleval;
    }

    // TODO: override toString function
}

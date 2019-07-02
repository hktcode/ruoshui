/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * the 'Delete' Logical Replication Message with full old tuples.
 */
public class LogicalOldTupleDeleteMsg extends LogicalTupleDeleteMsg
{
    /**
     * Obtain a LogicalOldTupleDeleteMsg from a relident, newtuple and keytuple.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param oldtuple old tuple.
     * @return a LogicalOldTupleDeleteMsg Object.
     * @throws ArgumentNullException if the {@code oldtuple} parameter is null.
     */
    public static LogicalOldTupleDeleteMsg of //
        /* */( long relident //
        /* */, ImmutableList<JsonNode> oldtuple //
        /* */)
    {
        if (oldtuple == null) {
            throw new ArgumentNullException("oldtuple");
        }
        return new LogicalOldTupleDeleteMsg(relident, oldtuple);
    }

    /**
     * the old tuple.
     */
    public final ImmutableList<JsonNode> oldtuple;

    /**
     * Contructor funciton.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param oldtuple old tuple.
     */
    private LogicalOldTupleDeleteMsg //
        /* */( long relident //
        /* */, ImmutableList<JsonNode> oldtuple //
        /* */)
    {
        super(relident);
        this.oldtuple = oldtuple;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String initial = String.format("delete-old:%d:", this.relident);
        StringBuilder sb = new StringBuilder(initial);
        LogicalMsg.toString(sb, this.oldtuple);
        return sb.toString();
    }
}

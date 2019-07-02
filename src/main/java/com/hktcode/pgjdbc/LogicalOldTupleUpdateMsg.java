/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * the 'Update' Logical Replication Message with full old tuples.
 */
public class LogicalOldTupleUpdateMsg extends LogicalTupleUpdateMsg
{
    /**
     * Obtain a LogicalOldTupleUpdateMsg from a relident, newtuple and keytuple.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param newtuple new tuple.
     * @param oldtuple old tuple.
     * @return a LogicalOldTupleUpdateMsg Object.
     * @throws ArgumentNullException if the {@code newtuple} or {@code oldtuple} parameter is null.
     */
    public static LogicalOldTupleUpdateMsg of //
        /* */( long relident //
        /* */, ImmutableList<JsonNode> newtuple //
        /* */, ImmutableList<JsonNode> oldtuple //
        /* */)
    {
        if (newtuple == null) {
            throw new ArgumentNullException("newtuple");
        }
        if (oldtuple == null) {
            throw new ArgumentNullException("oldtuple");
        }
        return new LogicalOldTupleUpdateMsg(relident, newtuple, oldtuple);
    }

    /**
     * the old tuple.
     */
    public final ImmutableList<JsonNode> oldtuple;

    /**
     * Contructor funciton.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param newtuple new tuple.
     * @param oldtuple old tuple.
     */
    private LogicalOldTupleUpdateMsg //
        /* */( long relident //
        /* */, ImmutableList<JsonNode> newtuple //
        /* */, ImmutableList<JsonNode> oldtuple //
        /* */)
    {
        super(relident, newtuple);
        this.oldtuple = oldtuple;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String initial = String.format("update-old:%d:", this.relident);
        StringBuilder sb = new StringBuilder(initial);
        LogicalMsg.toString(sb, this.newtuple);
        sb.append(':');
        LogicalMsg.toString(sb, this.oldtuple);
        return sb.toString();
    }
}

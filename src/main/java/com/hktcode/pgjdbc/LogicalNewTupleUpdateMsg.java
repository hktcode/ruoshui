/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * the 'Update' Logical Replication Message with no old tuples.
 */
public class LogicalNewTupleUpdateMsg extends LogicalTupleUpdateMsg
{
    /**
     * Obtain a LogicalNewTupleUpdateMsg from a relident, newtuple and keytuple.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param newtuple new tuple.
     * @return a LogicalNewTupleUpdateMsg Object.
     * @throws ArgumentNullException if the {@code newtuple} parameter is null.
     */
    public static LogicalNewTupleUpdateMsg of //
        /* */( long relident //
        /* */, ImmutableList<JsonNode> newtuple //
        /* */)
    {
        if (newtuple == null) {
            throw new ArgumentNullException("newtuple");
        }
        return new LogicalNewTupleUpdateMsg(relident, newtuple);
    }

    /**
     * Contructor funciton.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param newtuple new tuple.
     */
    private LogicalNewTupleUpdateMsg //
        /* */( long relident //
        /* */, ImmutableList<JsonNode> newtuple //
        /* */)
    {
        super(relident, newtuple);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String initial = String.format("update-new:%s:%d:", this.relident);
        StringBuilder sb = new StringBuilder(initial);
        LogicalMsg.toString(sb, this.newtuple);
        return sb.toString();
    }
}

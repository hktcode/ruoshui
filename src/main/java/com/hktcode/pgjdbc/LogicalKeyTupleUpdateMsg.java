/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * the 'Update' Logical Replication Message with only key tuples.
 */
public class LogicalKeyTupleUpdateMsg extends LogicalTupleUpdateMsg
{
    /**
     * Obtain a LogicalKeyTupleUpdateMsg from a relident, newtuple and keytuple.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param newtuple new tuple.
     * @param keytuple key tuple.
     * @return a LogicalKeyTupleUpdateMsg Object.
     * @throws ArgumentNullException if the {@code newtuple} or {@code keytuple} parameter is null.
     */
    public static LogicalKeyTupleUpdateMsg of //
        /* */( long relident //
        /* */, ImmutableList<JsonNode> newtuple //
        /* */, ImmutableList<JsonNode> keytuple //
        /* */)
    {
        if (newtuple == null) {
            throw new ArgumentNullException("newtuple");
        }
        if (keytuple == null) {
            throw new ArgumentNullException("keytuple");
        }
        return new LogicalKeyTupleUpdateMsg(relident, newtuple, keytuple);
    }

    /**
     * the keytuple.
     */
    public final ImmutableList<JsonNode> keytuple;

    /**
     * contructor function.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param newtuple new tuple.
     * @param keytuple key tuple.
     */
    private LogicalKeyTupleUpdateMsg //
        /* */( long relident //
        /* */, ImmutableList<JsonNode> newtuple //
        /* */, ImmutableList<JsonNode> keytuple //
        /* */)
    {
        super(relident, newtuple);
        this.keytuple = keytuple;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String initial = String.format("update-key:%d:", this.relident);
        StringBuilder sb = new StringBuilder(initial);
        LogicalMsg.toString(sb, this.newtuple);
        sb.append(':');
        LogicalMsg.toString(sb, this.keytuple);
        return sb.toString();
    }
}

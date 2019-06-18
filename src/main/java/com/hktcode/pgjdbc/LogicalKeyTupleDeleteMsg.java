/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * the 'Delete' Logical Replication Message with only key tuples.
 */
public class LogicalKeyTupleDeleteMsg extends LogicalTupleDeleteMsg
{
    /**
     * Obtain a LogicalKeyTupleDeleteMsg from a relident and keytuple.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param keytuple key tuple.
     * @return a LogicalKeyTupleDeleteMsg Object.
     * @throws ArgumentNullException if the {@code keytuple} parameter is null.
     */
    public static LogicalKeyTupleDeleteMsg of //
        /* */( long relident //
        /* */, ImmutableList<JsonNode> tuplekey //
        /* */)
    {
        if (tuplekey == null) {
            throw new ArgumentNullException("tuplekey");
        }
        return new LogicalKeyTupleDeleteMsg(relident, tuplekey);
    }

    /**
     * the keytuple.
     */
    public final ImmutableList<JsonNode> keytuple;

    /**
     * contructor function.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param keytuple key tuple.
     */
    private LogicalKeyTupleDeleteMsg //
        /* */( long relident //
        /* */, ImmutableList<JsonNode> keytuple //
        /* */)
    {
        super(relident);
        this.keytuple = keytuple;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String initial = String.format("delete-key:%d:", this.relident);
        StringBuilder sb = new StringBuilder(initial);
        LogicalMsg.toString(sb, this.keytuple);
        return sb.toString();
    }
}

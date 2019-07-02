/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.ImmutableIntArray;
import com.google.common.primitives.ImmutableLongArray;
import com.hktcode.lang.exception.ArgumentNullException;

import java.nio.ByteBuffer;

/**
 * the 'Update' Logical Replication Message.
 */
public class LogicalTupleUpdateMsg implements LogicalMsg
{
    /**
     * Obtain a LogicalTupleUpdateMsg from a ByteBuffer.
     *
     * @param content the ByteBuffer from Logical Replication Message.
     * @return a LogicalTupleUpdateMsg Object.
     * @throws ArgumentNullException if the {@code content} parameter is null.
     * @throws LogicalMsgFormatException if read the unexpected data from {@code content}
     */
    public static LogicalTupleUpdateMsg ofLogicalWal(ByteBuffer content)
    {
        if (content == null) {
            throw new ArgumentNullException("content");
        }
        long relident = content.getInt();
        byte b = content.get();
        ImmutableList<JsonNode> deltuple = LogicalMsg.getTuple(content);
        if (b == 'N') {
            return LogicalNewTupleUpdateMsg.of(relident, deltuple);
        }
        if (b != 'K' && b != 'O') {
            throw new LogicalMsgFormatException(ImmutableLongArray.of('K', 'O'), b);
        }
        byte n = content.get();
        if (n != 'N') {
            throw new LogicalMsgFormatException(ImmutableLongArray.of('N'), n);
        }
        ImmutableList<JsonNode> newtuple = LogicalMsg.getTuple(content);
        if (b == 'K') {
            return LogicalKeyTupleUpdateMsg.of(relident, newtuple, deltuple);
        }
        else {
            return LogicalOldTupleUpdateMsg.of(relident, newtuple, deltuple);
        }
    }

    /**
     * ID of the relation corresponding to the ID in the relation message.
     */
    public final long relident;

    /**
     * New tuple.
     */
    public final ImmutableList<JsonNode> newtuple;

    /**
     * contructor function.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param newtuple new tuple
     */
    protected LogicalTupleUpdateMsg //
        /* */( long relident //
        /* */, ImmutableList<JsonNode> newtuple //
        /* */)
    {
        this.relident = relident;
        this.newtuple = newtuple;
    }
}

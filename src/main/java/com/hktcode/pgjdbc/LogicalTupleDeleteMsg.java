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
 * the 'Delete' Logical Replication Message.
 */
public abstract class LogicalTupleDeleteMsg implements LogicalMsg
{
    /**
     * Obtain a LogicalTupleDeleteMsg from a ByteBuffer.
     *
     * @param content the ByteBuffer from Logical Replication Message.
     * @return a LogicalTupleDeleteMsg Object.
     * @throws ArgumentNullException if the {@code content} parameter is null.
     * @throws LogicalMsgFormatException if read the unexpected data from {@code content}.
     */
    public static LogicalTupleDeleteMsg ofLogicalWal(ByteBuffer content)
    {
        if (content == null) {
            throw new ArgumentNullException("content");
        }
        long relident = content.getInt();
        byte b = content.get();
        if (b == 'K') {
            ImmutableList<JsonNode> tupleval = LogicalMsg.getTuple(content);
            return LogicalKeyTupleDeleteMsg.of(relident, tupleval);
        }
        else if (b == 'O'){
            ImmutableList<JsonNode> tupleval = LogicalMsg.getTuple(content);
            return LogicalOldTupleDeleteMsg.of(relident, tupleval);
        }
        else {
            throw new LogicalMsgFormatException(ImmutableLongArray.of('K', 'O'), b);

        }
    }

    /**
     * ID of the relation corresponding to the ID in the relation message.
     */
    public final long relident;

    /**
     * contructor function.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     */
    protected LogicalTupleDeleteMsg(long relident)
    {
        this.relident = relident;
    }
}

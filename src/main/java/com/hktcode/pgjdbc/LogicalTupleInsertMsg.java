/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.ImmutableLongArray;
import com.hktcode.lang.exception.ArgumentNullException;

import java.nio.ByteBuffer;

/**
 * the 'Insert' Logical Replication Message.
 */
public class LogicalTupleInsertMsg implements LogicalMsg
{
    /**
     * Obtain a LogicalTupleInsertMsg from a lsnofcmt, committs and xidofmsg.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param tupleval new tuple
     * @return a LogicalTupleInsertMsg Object
     * @throws ArgumentNullException if {@code tupleval} is {@code null}.
     */
    public static LogicalTupleInsertMsg of(long relident, ImmutableList<JsonNode> tupleval)
    {
        if (tupleval == null) {
            throw new ArgumentNullException("tupleval");
        }
        return new LogicalTupleInsertMsg(relident, tupleval);
    }

    /**
     * Obtain a LogicalTupleInsertMsg from a ByteBuffer.
     *
     * @param content the ByteBuffer from Logical Replication Message.
     * @return a LogicalTupleInsertMsg Object.
     * @throws ArgumentNullException if the {@code content} parameter is null.
     * @throws LogicalMsgFormatException if read the unexpected data from {@code content}
     */
    public static LogicalTupleInsertMsg ofLogicalWal(ByteBuffer content)
    {
        if (content == null) {
            throw new ArgumentNullException("content");
        }
        long relident = content.getInt();
        byte b = content.get();
        if (b != 'N') {
            throw new LogicalMsgFormatException(ImmutableLongArray.of('N'), b);
        }
        ImmutableList<JsonNode> tupleval = LogicalMsg.getTuple(content);
        return new LogicalTupleInsertMsg(relident, tupleval);
    }

    /**
     * ID of the relation corresponding to the ID in the relation message.
     */
    public final long relident;

    /**
     * New tuple.
     */
    public final ImmutableList<JsonNode> tupleval;

    /**
     * contructor function.
     *
     * @param relident ID of the relation corresponding to the ID in the relation message.
     * @param tupleval new tuple
     */
    private LogicalTupleInsertMsg //
        /* */( long relident //
        /* */, ImmutableList<JsonNode> tupleval //
        /* */)
    {
        this.relident = relident;
        this.tupleval = tupleval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String initial = String.format("%12s:%d:", "insert", this.relident);
        StringBuilder sb = new StringBuilder(initial);
        LogicalMsg.toString(sb, this.tupleval);
        return sb.toString();
    }
}

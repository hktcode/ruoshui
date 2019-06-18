/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.google.common.primitives.ImmutableLongArray;
import com.hktcode.lang.exception.ArgumentNullException;

import java.nio.ByteBuffer;

/**
 * the 'Truncate" Logical Replication Message.
 */
public class LogicalRelTruncateMsg implements LogicalMsg
{
    /**
     * Obtain a LogicalRelTruncateMsg from an optionbs and relalist.
     *
     * @param optionbs Option bits for TRUNCATE.
     * @param relalist ID of the relation corresponding to the ID in the relation message.
     * @return a LogicalRelTruncateMsg Object.
     * @throws ArgumentNullException if the {@code relalist} parameter is null.
     */
    public static LogicalRelTruncateMsg of(long optionbs, ImmutableLongArray relalist)
    {
        if (relalist == null) {
            throw new ArgumentNullException("relalist");
        }
        return new LogicalRelTruncateMsg(optionbs, relalist);
    }

    /**
     * Obtain a LogicalRelTruncateMsg from a ByteBuffer.
     *
     * @param content the ByteBuffer from Logical Replication Message.
     * @return a LogicalRelTruncateMsg Object.
     * @throws ArgumentNullException if the {@code content} parameter is null.
     */
    public static LogicalRelTruncateMsg ofLogicalWal(ByteBuffer content)
    {
        if (content == null) {
            throw new ArgumentNullException("content");
        }
        int size = content.getInt();
        long optionbs = Byte.toUnsignedLong(content.get());
        long[] relalist = new long[size];
        for (int i = 0; i < relalist.length; ++i) {
            long relident = content.getInt();
            relalist[i] = relident;
        }
        return new LogicalRelTruncateMsg(optionbs, ImmutableLongArray.copyOf(relalist));
    }

    /**
     * Option bits for TRUNCATE.
     *
     * - 1 for CASCADE
     * - 2 for RESTART IDENTITY
     */
    public final long optionbs;

    /**
     * IDs of the relation corresponding to the ID in the relation message.
     */
    public final ImmutableLongArray relalist;

    /**
     * Contrunctor function.
     *
     * @param optionbs Option bits for TRUNCATE.
     * @param relalist ID of the relation corresponding to the ID in the relation message.
     */
    private LogicalRelTruncateMsg(long optionbs, ImmutableLongArray relalist)
    {
        this.optionbs = optionbs;
        this.relalist = relalist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final String format = "%12s:%d:";
        StringBuilder sb = new StringBuilder(
            String.format(format, "truncate", this.optionbs));
        char splitter = '[';
        for (int i = 0; i < this.relalist.length(); ++i) {
            sb.append(this.relalist.get(i));
            sb.append(splitter);
            splitter = ',';
        }
        sb.append(']');
        return sb.toString();
    }
}

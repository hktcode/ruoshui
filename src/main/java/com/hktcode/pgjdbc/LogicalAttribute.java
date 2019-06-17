/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.hktcode.lang.exception.ArgumentNullException;

import java.nio.ByteBuffer;

/**
 * an attribute of relation message in Logical Replication Message.
 */
public class LogicalAttribute
{
    /**
     * Obtain a LogicalAttribute from an attflags, attrname, datatype and attypmod.
     *
     * @param attflags flags for the column.
     * @param attrname Name of the column.
     * @param datatype ID of the column's data type.
     * @param attypmod Type modifier of the column (atttypmod).
     *
     * @return a LogicalAttribute Object.
     */
    public static LogicalAttribute of //
        /* */( long attflags //
        /* */, String attrname //
        /* */, long datatype //
        /* */, long attypmod //
        /* */)
    {
        if (attrname == null) {
            throw new ArgumentNullException("attrname");
        }
        return new LogicalAttribute(attflags, attrname, datatype, attypmod);
    }

    /**
     * Obtain a LogicalAttribute from a ByteBuffer.
     *
     * @param content the ByteBuffer from Logical Replication Message.
     * @return a LogicalAttribute Object.
     * @throws ArgumentNullException if the {@code content} parameter is null.
     */
    public static LogicalAttribute ofLogicalWal(ByteBuffer content)
    {
        if (content == null) {
            throw new ArgumentNullException("content");
        }
        long attflags = Byte.toUnsignedLong(content.get());
        String attrname = LogicalMsg.readCStyleUtf8String(content);
        long datatype = content.getInt();
        long attypmod = content.getInt();
        return new LogicalAttribute(attflags, attrname, datatype, attypmod);
    }

    /**
     * Flags for the column.
     *
     * Currently can be either 0 for no flags or 1 which marks the column as
     * part of the key.
     */
    public final long attflags;

    /**
     * Name of the column.
     */
    public final String attrname;

    /**
     * ID of the column's data type.
     */
    public final long datatype;

    /**
     * Type modifier of the column (atttypmod).
     */
    public final long attypmod;

    /**
     * contructor function.
     *
     * @param attflags flags for the column.
     * @param attrname Name of the column.
     * @param datatype ID of the column's data type.
     * @param attypmod Type modifier of the column (atttypmod).
     */
    private LogicalAttribute //
        /* */( long attflags
        /* */, String attrname
        /* */, long datatype
        /* */, long attypmod
        /* */)
    {
        this.attflags = attflags;
        this.attrname = attrname;
        this.datatype = datatype;
        this.attypmod = attypmod;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        toString(builder);
        return builder.toString();
    }

    /**
     * put the string representation into a {@code StringBuilder}.
     *
     * @param builder the {@code StringBuilder} to append the string representation.
     */
    public void toString(StringBuilder builder)
    {
        if (builder == null) {
            throw new ArgumentNullException("builder");
        }
        builder.append("column:");
        builder.append(this.attrname);
        builder.append(':');
        builder.append(this.datatype);
        builder.append(':');
        builder.append(this.attflags);
        builder.append(':');
        builder.append(this.attypmod);
    }
}

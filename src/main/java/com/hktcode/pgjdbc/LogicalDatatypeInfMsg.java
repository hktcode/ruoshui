/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.hktcode.lang.exception.ArgumentNullException;

import java.nio.ByteBuffer;

/**
 * the 'Type' logical replication message.
 */
public class LogicalDatatypeInfMsg implements LogicalMsg
{
    /**
     * Obtain a LogicalDatatypeInfMsg from a datatypem, tpschema and typename.
     *
     * @param datatype ID of the data type.
     * @param tpschema Namespace (empty string for pg_catalog).
     * @param typename Name of the data type.
     *
     * @return a LogicalDatatypeInfMsg Object.
     * @throws ArgumentNullException if {@code tpschema} or {@code typename} is {@code null}.
     */
    public static LogicalDatatypeInfMsg //
    of(long datatype, String tpschema, String typename)
    {
        if (tpschema == null) {
            throw new ArgumentNullException("tpschema");
        }
        if (typename == null) {
            throw new ArgumentNullException("typename");
        }
        // TODO: namespace typename format
        return new LogicalDatatypeInfMsg(datatype, tpschema, typename);
    }

    /**
     * Obtain a LogicalDatatypeInfMsg from a ByteBuffer.
     *
     * @param content the ByteBuffer from Logical Replication Message.
     * @return a LogicalDatatypeInfMsg Object.
     * @throws ArgumentNullException if the {@code content} parameter is null.
     */
    public static LogicalDatatypeInfMsg ofLogicalWal(ByteBuffer content)
    {
        if (content == null) {
            throw new ArgumentNullException("content");
        }
        long datatype = content.getInt();
        String tpschema = LogicalMsg.readCStyleUtf8String(content);
        String typename = LogicalMsg.readCStyleUtf8String(content);
        if ("".equals(tpschema)) {
            tpschema = "pg_catalog";
        }
        return new LogicalDatatypeInfMsg(datatype, tpschema, typename);
    }

    /**
     * ID of the data type.
     */
    public final long datatype;

    /**
     * Namespace (empty string for pg_catalog).
     */
    public final String tpschema;

    /**
     * Name of the data type.
     */
    public final String typename;

    /**
     * contructor function.
     *
     * @param datatype ID of the data type.
     * @param tpschema Namespace (empty string for pg_catalog).
     * @param typename Name of the data type.
     */
    private LogicalDatatypeInfMsg(long datatype, String tpschema, String typename)
    {
        this.datatype = datatype;
        this.tpschema = tpschema;
        this.typename = typename;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final String format = "%12s:%s.%s:%d";
        return String.format(format, "type", tpschema, typename, datatype);
    }
}

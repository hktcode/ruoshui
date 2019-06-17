/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.nio.ByteBuffer;

/**
 * the 'Origin' Logical Replication Message
 */
public class LogicalOriginNamesMsg implements LogicalMsg
{
    /**
     * Obtain a LogicalOriginNamesMsg from a lsnofcmt and orgnname.
     *
     * @param lsnofcmt The LSN of the commit on the origin server.
     * @param orgnname name of the origin.
     * @return a LogicalOriginNamesMsg Object.
     * @throws ArgumentNullException if the {@code orgnname} argument is {@code null}
     */
    public static LogicalOriginNamesMsg of(long lsnofcmt, String orgnname)
    {
        if (orgnname == null) {
            throw new ArgumentNullException("orgnname");
        }
        // TODO: orgnname formatter
        return new LogicalOriginNamesMsg(lsnofcmt, orgnname);
    }

    /**
     * Obtain a LogicalOriginNamesMsg from a ByteBuffer.
     *
     * @param content the ByteBuffer from Logical Replication Message.
     * @return a LogicalOriginNamesMsg Object.
     * @throws ArgumentNullException if the {@code content} parameter is null.
     */
    public static LogicalOriginNamesMsg ofLogicalWal(ByteBuffer content)
    {
        if (content == null) {
            throw new ArgumentNullException("content");
        }
        long lsnofcmt = content.getLong();
        String orgnname = LogicalMsg.readCStyleUtf8String(content);
        return new LogicalOriginNamesMsg(lsnofcmt, orgnname);
    }

    /**
     * The LSN of the commit on the origin server.
     */
    public final long lsnofcmt;

    /**
     * Name of the origin.
     */
    public final String orgnname;

    /**
     * constructor function.
     *
     * @param lsnofcmt the LSN of the commit on the origin server.
     * @param orgnname Name of the origin.
     */
    private LogicalOriginNamesMsg(long lsnofcmt, String orgnname)
    {
        this.lsnofcmt = lsnofcmt;
        this.orgnname = orgnname;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final String format = "%12s:%s:%s";
        return String.format(format //
            , "origin" //
            , LogSequenceNumber.valueOf(this.lsnofcmt) //
            , this.orgnname //
        );
    }
}

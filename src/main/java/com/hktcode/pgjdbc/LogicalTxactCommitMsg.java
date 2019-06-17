/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.nio.ByteBuffer;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * the 'Commit' Logical Replication Message.
 */
public class LogicalTxactCommitMsg implements LogicalMsg
{
    /**
     * Obtain a LogicalTxactCommitMsg from a xidflags, lsnofcmt, endofxid and committs.
     *
     * @param xidflags flags
     * @param lsnofcmt the lsn of commit
     * @param endofxid The end LSN of the transaction.
     * @param committs Commit timestamp of the transaction.
     * @return a LogicalTxactCommitMsg Object
     */
    public static LogicalTxactCommitMsg //
    of(long xidflags, long lsnofcmt, long endofxid, long committs)
    {
        return new LogicalTxactCommitMsg(xidflags, lsnofcmt, endofxid, committs);
    }

    /**
     * Obtain a LogicalTxactCommitMsg from a ByteBuffer.
     *
     * @param content the ByteBuffer from Logical Replication Message.
     * @return a LogicalTxactCommitMsg Object.
     * @throws ArgumentNullException if the {@code content} parameter is null.
     */
    public static LogicalTxactCommitMsg ofLogicalWal(ByteBuffer content)
    {
        if (content == null) {
            throw new ArgumentNullException("content");
        }
        long xidflags = Byte.toUnsignedLong(content.get());
        long lsnofcmt = content.getLong();
        long endofxid = content.getLong();
        long committs = content.getLong();
        return new LogicalTxactCommitMsg(xidflags, lsnofcmt, endofxid, committs);
    }

    /**
     * Flags.
     *
     * currently unused (must be 0).
     */
    public final long xidflags;

    /**
     * The LSN of the commit
     */
    public final long lsnofcmt;

    /**
     * The end LSN of the transaction.
     */
    public final long endofxid;

    /**
     * Commit timestamp of the transaction.
     *
     * The value is in number of microseconds since PostgreSQL epoch (2000-01-01).
     */
    public final long committs;

    /**
     * constructor funcation.
     *
     * @param xidflags flags
     * @param lsnofcmt the lsn of commit
     * @param endofxid The end LSN of the transaction.
     * @param committs Commit timestamp of the transaction.
     */
    private LogicalTxactCommitMsg //
    (long xidflags, long lsnofcmt, long endofxid, long committs)
    {
        this.xidflags = xidflags;
        this.lsnofcmt = lsnofcmt;
        this.endofxid = endofxid;
        this.committs = committs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final String format = "%12s:%s:%s:%d:%s";
        String datetime = PostgreSQL.EPOCH.plus(this.committs, ChronoUnit.MICROS) //
            .format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        return String.format(format, "commit"
            , LogSequenceNumber.valueOf(this.lsnofcmt) //
            , LogSequenceNumber.valueOf(this.endofxid) //
            , this.xidflags, datetime);
    }
}

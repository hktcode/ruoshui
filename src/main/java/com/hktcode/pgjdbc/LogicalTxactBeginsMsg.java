/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.nio.ByteBuffer;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * the 'Begin' Logical Replication Message.
 */
public class LogicalTxactBeginsMsg implements LogicalMsg
{
    /**
     * Obtain a LogicalTxactBeginsMsg from a lsnofcmt, committs and xidofmsg.
     *
     * @param lsnofcmt The final LSN of the transaction.
     * @param committs Commit timestamp of the transaction(base on the Postgresql epoch, 2000-01-01).
     * @param xidofmsg Xid of the transaction.
     * @return a LogicalTxactBeginsMsg Object
     */
    public static LogicalTxactBeginsMsg of(long lsnofcmt, long committs, long xidofmsg)
    {
        return new LogicalTxactBeginsMsg(lsnofcmt, committs, xidofmsg);
    }

    /**
     * Obtain a LogicalTxactBeginsMsg from a ByteBuffer.
     *
     * @param content the ByteBuffer from Logical Replication Message.
     * @return a LogicalTxactBeginsMsg Object.
     * @throws ArgumentNullException if the {@code content} parameter is null.
     */
    public static LogicalTxactBeginsMsg ofLogicalWal(ByteBuffer content)
    {
        if (content == null) {
            throw new ArgumentNullException("content");
        }
        long lsnofcmt = content.getLong();
        long committs = content.getLong();
        long xidofmsg = content.getInt();
        return new LogicalTxactBeginsMsg(lsnofcmt, committs, xidofmsg);
    }

    /**
     * The final LSN of the transaction.
     */
    public final long lsnofcmt;

    /**
     * Commit timestamp of the transaction.
     *
     * The value is in number of microseconds since PostgreSQL epoch(2000-01-01).
     *
     * @see PostgreSQL#EPOCH
     */
    public final long committs;

    /**
     * Xid of the transaction.
     */
    public final long xidofmsg;

    /**
     * constructor funcation.
     *
     * @param lsnofcmt the final LSN of the transaction.
     * @param committs Commit timestamp of the transaction.
     * @param xidofmsg xid of the transaction.
     */
    private LogicalTxactBeginsMsg(long lsnofcmt, long committs, long xidofmsg)
    {
        this.lsnofcmt = lsnofcmt;
        this.committs = committs;
        this.xidofmsg = xidofmsg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final String format = "%12s:%s:%d:%s";
        String committsstr = PostgreSQL.EPOCH //
            .plus(this.committs, ChronoUnit.MICROS) //
            .withZoneSameInstant(ZoneId.systemDefault()) //
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return String.format(format, "begin"
            , LogSequenceNumber.valueOf(this.lsnofcmt), this.xidofmsg, committsstr);
    }
}

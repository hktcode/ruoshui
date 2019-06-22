/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import org.postgresql.replication.LogSequenceNumber;

import java.math.BigInteger;

/**
 * 事务开始消息.
 */
public class PgsqlTxactBeginsVal extends PgsqlTxactionVal
{
    /**
     * 根据提交LSN、逻辑复制流中的事务开始消息和逻辑复制上下文构建{@link PgsqlTxactBeginsVal}对象.
     *
     * @param lsn 该消息在wal中的位置.
     * @param msg 逻辑复制流中的事务开始消息.
     * @param ctx 逻辑复制上下文.
     *
     * @return 根据提交LSN、逻辑复制流中的事务消息和逻辑复制上下文构建的{@link PgsqlTxactBeginsVal}对象.
     * @throws ArgumentNullException if {@code msg} or {@code ctx} is {@code null}.
     */
    public static ImmutableList<PgsqlVal>
    of(long lsn, LogicalTxactBeginsMsg msg, LogicalTxactContext ctx)
    {
        if (msg == null) {
            throw new ArgumentNullException("lsn");
        }
        if (ctx == null) {
            throw new ArgumentNullException("ctx");
        }
        ctx.committs = msg.committs;
        ctx.xidofmsg = msg.xidofmsg;
        ctx.lsnofcmt = msg.lsnofcmt;
        PgsqlTxactBeginsVal val = new PgsqlTxactBeginsVal //
            /* */( ctx.dbserver //
            /* */, msg.xidofmsg //
            /* */, msg.committs //
            /* */, lsn //
            /* */);
        return ImmutableList.of(val);
    }

    /**
     * 类型协议号.
     */
    public static final long PROTOCOL = 2L;

    /**
     * 类型的名称.
     */
    public static final String TYPENAME = "PgsqlTxactBegins";

    /**
     * 该消息在WAL中的起始位置.
     */
    public final long lsnofmsg;

    /**
     * 构造函数.
     *
     * @param dbserver 服务器唯一标识.
     * @param xidofmsg 消息所在事务编号.
     * @param committs 消息提交的PostgreSQL纪元时间戳.
     * @param lsnofmsg 该消息在WAL中的起始位置.
     */
    private PgsqlTxactBeginsVal //
        /* */( String dbserver //
        /* */, long xidofmsg //
        /* */, long committs //
        /* */, long lsnofmsg //
        /* */)
    {
        super(dbserver, xidofmsg, committs);
        this.lsnofmsg = lsnofmsg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getProtocol()
    {
        return PROTOCOL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypename()
    {
        return TYPENAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectNode toObjectNode()
    {
        ObjectNode result = super.toObjectNode();
        result.put("lsnofmsg", new BigInteger(Long.toUnsignedString(this.lsnofmsg)));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        super.appendTo(builder);
        builder.append('|');
        builder.append(LogSequenceNumber.valueOf(lsnofmsg));
        return builder.toString();
    }
}

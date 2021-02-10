/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalTxactCommitMsg;

import java.math.BigInteger;

/**
 * 事务提交消息.
 */
public class PgsqlValTxactCommit extends PgsqlValTxaction
{
    /**
     * 根据提交LSN、逻辑复制流中的事务提交消息和逻辑复制上下文构建{@link PgsqlValTxactCommit}对象.
     *
     * @param lsn 该消息在wal中的位置.
     * @param msg 逻辑复制流中的事务提交消息.
     * @param ctx 逻辑复制上下文.
     *
     * @return 根据提交LSN、逻辑复制流中的事务消息和逻辑复制上下文构建的{@link PgsqlValTxactCommit}对象.
     * @throws ArgumentNullException if {@code msg} or {@code ctx} is {@code null}.
     */
    public static ImmutableList<PgsqlVal>
    of(long lsn, LogicalTxactCommitMsg msg, LogicalTxactContext ctx)
    {
        if (msg == null) {
            throw new ArgumentNullException("lsn");
        }
        if (ctx == null) {
            throw new ArgumentNullException("ctx");
        }
        PgsqlValTxactCommit val = new PgsqlValTxactCommit //
            /* */( ctx.dbserver //
            /* */, ctx.xidofmsg
            /* */, msg.committs //
            /* */, lsn //
            /* */, msg.xidflags
            /* */);
        return ImmutableList.of(val);
    }

    /**
     * 类型协议号.
     */
    public static final long PROTOCOL = 3L;

    /**
     * 类型的名称.
     */
    public static final String TYPENAME = "PgsqlTxactCommit";

    /**
     * 提交的标记.
     */
    public final long xidflags;

    /**
     * 构造函数.
     *
     * @param dbserver 服务器唯一标识.
     * @param xidofmsg 消息所在事务编号.
     * @param committs 消息提交的PostgreSQL纪元时间戳.
     * @param lsnofmsg 该消息在WAL中的起始位置.
     * @param xidflags 提交的标记.
     */
    private PgsqlValTxactCommit //
        /* */( String dbserver //
        /* */, long xidofmsg //
        /* */, long committs //
        /* */, long lsnofmsg //
        /* */, long xidflags //
        /* */)
    {
        super(dbserver, lsnofmsg, xidofmsg, committs);
        this.xidflags = xidflags;
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
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        super.appendTo(builder);
        builder.append('|');
        builder.append(this.xidflags);
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectNode toObjectNode()
    {
        ObjectNode result = super.toObjectNode();
        result.put("xidflags", new BigInteger(Long.toUnsignedString(this.xidflags)));
        return result;
    }
}

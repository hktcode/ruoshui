/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

import java.math.BigInteger;

/**
 * 表示在事务中的逻辑复制消息.
 */
public abstract class PgsqlValTxaction extends PgsqlVal
{
    /**
     * 该消息在WAL中的起始位置.
     */
    public final long lsnofmsg;

    /**
     * 该消息所在的事务标识.
     *
     * 在PostgreSQL中，xid是一个32位的无符号整数，这里采用{@code long}类型是为了以后扩展使用。
     * 注意，更大的{@code xidofmsg}可能会先到达，因此不能以{@code xidofmsg}的大小判断消息重复。
     */
    public final long xidofmsg;

    /**
     * 构造函数.
     *
     * @param dbserver 服务器唯一标识.
     * @param xidofmsg 消息所在事务编号.
     */
    protected PgsqlValTxaction(String dbserver, long lsnofmsg, long xidofmsg)
    {
        super(dbserver);
        this.lsnofmsg = lsnofmsg;
        this.xidofmsg = xidofmsg;
    }

    /**
     * 将{@code PgsqlVal}特性形式的字符串添加到{@code builder}后面.
     *
     * 一般用于生成{@link #toString()}字符串中人类可读的形式.
     *
     * @param builder 要添加的{@code StringBuilder}对象.
     */
    protected void appendTo(StringBuilder builder)
    {
        super.appendTo(builder);
        builder.append('|');
        builder.append(this.xidofmsg);
        builder.append('|');
        builder.append(this.lsnofmsg);
    }

    /**
     * 转换成{@link ObjectNode}形式.
     *
     * @return {@link ObjectNode}对象.
     */
    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node = super.toJsonObject(node);
        node.put("lsnofmsg", new BigInteger(Long.toUnsignedString(this.lsnofmsg)));
        node.put("xidofmsg", new BigInteger(Long.toUnsignedString(this.xidofmsg)));
        return node;
    }
}

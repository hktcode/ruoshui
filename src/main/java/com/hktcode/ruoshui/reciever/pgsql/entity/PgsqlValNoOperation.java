/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.entity;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalNoOperationMsg;

/**
 * 无任何消息的{@code PgsqlVal}占位符.
 *
 * 加入本类只是为了整个体系的完整性考虑.
 */
public class PgsqlValNoOperation extends PgsqlVal
{
    /**
     * 根据{@link LogicalNoOperationMsg}和复制上下文构建{@link PgsqlValNoOperation}对象.
     *
     * @param msg {@link LogicalNoOperationMsg}对象.
     * @param ctx 逻辑复制上下文.
     *
     * @return 根据{@link LogicalNoOperationMsg}对象和复制上下文构建的{@link PgsqlValNoOperation}对象.
     * @throws ArgumentNullException if {@code ctx} is {@code null}.
     */
    public static ImmutableList<PgsqlVal>
    of (LogicalNoOperationMsg msg, LogicalTxactContext ctx)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        if (ctx == null) {
            throw new ArgumentNullException("ctx");
        }
        PgsqlValNoOperation val = new PgsqlValNoOperation(ctx.dbserver);
        return ImmutableList.of(val);
    }

    /**
     * 类型协议号.
     */
    public static final long PROTOCOL = 0L;

    /**
     * 类型的名称.
     */
    public static final String TYPENAME = "PgsqlNoOperation";

    /**
     * 构造函数.
     *
     * @param dbserver 服务器唯一标识.
     */
    private PgsqlValNoOperation(String dbserver)
    {
        super(dbserver);
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
}

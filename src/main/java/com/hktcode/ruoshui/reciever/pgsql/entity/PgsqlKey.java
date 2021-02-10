/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.entity;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigInteger;

/**
 * 用于表示生成在外部队列（例如Kafka）中一条记录的Key.
 */
public class PgsqlKey
{
    /**
     * 生成函数.
     *
     * @param lsnofcmt 事务或者快照提交的log sequence number.
     * @param sequence 事务内记录的顺序号.
     */
    public static PgsqlKey of(long lsnofcmt, long sequence)
    {
        return new PgsqlKey(lsnofcmt, sequence);
    }

    /**
     * 时间线.
     *
     * 参考PostgreSQL时间线的概念，0表示当前不支持时间线信息.
     */
    public final long timeline;

    /**
     * 该事务或快照提交的log sequence number.
     *
     * 该log sequence number所代表的WAL记录执行完之后，本记录所代表
     * 的事务或者快照生效.
     */
    public final long lsnofcmt;

    /**
     * 事务内的记录顺序号.
     */
    public final long sequence;

    /**
     * 构造函数.
     *
     * 目前不支持时间线功能，只是保留时间线作为后续扩展使用.
     *
     * @param lsnofcmt 事务或者快照提交的log sequence number.
     * @param sequence 事务内记录的顺序号.
     */
    private PgsqlKey(long lsnofcmt, long sequence)
    {
        this.timeline = 0;
        this.lsnofcmt = lsnofcmt;
        this.sequence = sequence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return String.format("%016x%016x|%016x", timeline, lsnofcmt, sequence);
    }

    /**
     * 转换成{@link ObjectNode}形式.
     *
     * @return {@link ObjectNode}对象.
     */
    public ObjectNode toObjectNode()
    {
        ObjectNode result = new ObjectNode(JsonNodeFactory.instance);
        result.put("timeline", timeline);
        result.put("lsnofcmt", new BigInteger(Long.toUnsignedString(lsnofcmt)));
        result.put("sequence", sequence);
        return result;
    }
}

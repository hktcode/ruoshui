/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.entity;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PostgreSQL;

import java.math.BigInteger;

/**
 * 用于表示生成在外部队列（例如Kafka）中一条记录的Key.
 */
public class PgsqlKey implements JacksonObject
{
    /**
     * 生成函数.
     *
     * @param lsnofcmt 事务或者快照提交的log sequence number.
     * @param sequence 事务内记录的顺序号.
     */
    public static PgsqlKey of(long lsnofcmt, long sequence, long committs)
    {
        return new PgsqlKey(lsnofcmt, sequence, committs);
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
     * 提交的PostgreSQL纪元时间戳.
     *
     * 这个值是自PostgreSQL纪元（{@code 2000-01-01}）以来的微秒数.
     *
     * 如果是快照，表示该时间后快照生效。如果是不是快照，表示该时间后修改生效.
     *
     * @see PostgreSQL#EPOCH
     */
    public final long committs;

    /**
     * 构造函数.
     *
     * 目前不支持时间线功能，只是保留时间线作为后续扩展使用.
     *
     * @param lsnofcmt 事务或者快照提交的log sequence number.
     * @param sequence 事务内记录的顺序号.
     * @param committs 消息提交的PostgreSQL纪元时间戳.
     */
    private PgsqlKey(long lsnofcmt, long sequence, long committs)
    {
        this.timeline = 0;
        this.lsnofcmt = lsnofcmt;
        this.sequence = sequence;
        this.committs = committs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String datetime = PostgreSQL.toZonedDatetime(this.committs).toString();
        return String.format("%016x%016x|%016x|%s", timeline, lsnofcmt, sequence, datetime);
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
        node.put("timeline", new BigInteger(Long.toUnsignedString(timeline)));
        node.put("lsnofcmt", new BigInteger(Long.toUnsignedString(lsnofcmt)));
        node.put("sequence", new BigInteger(Long.toUnsignedString(sequence)));
        long epoch = PostgreSQL.EPOCH.toInstant().toEpochMilli() * 1000;
        node.put("committs", new BigInteger(Long.toUnsignedString(committs + epoch)));
        return node;
    }
}

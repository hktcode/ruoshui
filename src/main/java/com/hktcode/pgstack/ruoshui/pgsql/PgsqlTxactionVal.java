/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.pgjdbc.PostgreSQL;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * 表示在事务中的逻辑复制消息.
 */
public abstract class PgsqlTxactionVal extends PgsqlVal
{
    /**
     * 该消息所在的事务标识.
     *
     * 在PostgreSQL中，xid是一个32位的无符号整数，这里采用{@code long}类型是为了以后扩展使用。
     * 注意，更大的{@code xidofmsg}可能会先到达，因此不能以{@code xidofmsg}的大小判断消息重复。
     */
    public final long xidofmsg;

    /**
     * 提交的PostgreSQL纪元时间戳.
     *
     * 这个值是自PostgreSQL纪元（{@code 2000-01-01}）以来的毫秒数目.
     *
     * 如果是快照，表示该时间后快照生效。如果是不是快照，表示该时间后修改生效.
     *
     * @see PostgreSQL#EPOCH
     */
    public final long committs;

    /**
     * 构造函数.
     *
     * @param dbserver 服务器唯一标识.
     * @param xidofmsg 消息所在事务编号.
     * @param committs 消息提交的PostgreSQL纪元时间戳.
     */
    protected PgsqlTxactionVal(String dbserver, long xidofmsg, long committs)
    {
        super(dbserver);
        this.xidofmsg = xidofmsg;
        this.committs = committs;
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
        ZonedDateTime commitdt = PostgreSQL.toZonedDatetime(this.committs);
        builder.append(commitdt);
    }

    /**
     * 转换成{@link ObjectNode}形式.
     *
     * @return {@link ObjectNode}对象.
     */
    public ObjectNode toObjectNode()
    {
        ObjectNode node = super.toObjectNode();
        node.put("xidofmsg", this.xidofmsg);
        ZonedDateTime commitdt = PostgreSQL.toZonedDatetime(this.committs) //
            .toOffsetDateTime().atZoneSameInstant(ZoneId.systemDefault());
        node.put("committs", commitdt.format(ISO_OFFSET_DATE_TIME));
        return node;
    }
}

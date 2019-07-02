/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 表示快照结束的消息.
 *
 * 当前PostgreSQL流复制协议中并不会产生这个消息。
 * 这个消息是本程序在获取快照时发出的。
 */
public class LogicalEndSnapshotMsg implements LogicalMsg
{
    /**
     * 根据指定的关系列表构建{@link LogicalEndSnapshotMsg}对象.
     *
     * @param relalist 快照内容中所列出的关系列表.
     *
     * @return 包含{@code relalist}中所指定的关系的快照开始的消息.
     * @throws ArgumentNullException if {@code relalist} is null.
     */
    public static LogicalEndSnapshotMsg of(ImmutableList<PgReplRelation> relalist)
    {
        if (relalist == null) {
            throw new ArgumentNullException("relalist");
        }
        return new LogicalEndSnapshotMsg(relalist);
    }

    /**
     * 快照内容中所列出的关系列表.
     */
    public final ImmutableList<PgReplRelation> relalist;

    /**
     * 构造函数.
     *
     * @param relalist 快照内容总所列出的关系列表.
     */
    private LogicalEndSnapshotMsg(ImmutableList<PgReplRelation> relalist)
    {
        this.relalist = relalist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        final String format = "%12s";
        return String.format(format, "snapshot-starts");
    }
}

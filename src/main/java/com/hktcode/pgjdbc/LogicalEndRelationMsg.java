/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgjdbc;

import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 表示结束某个关系快照的消息.
 *
 * 当前PostgreSQL流复制协议中并不会产生这个消息。
 * 这个消息是本程序在获取快照时发出的。
 */
public class LogicalEndRelationMsg implements LogicalMsg
{
    /**
     * 根据指定的关系构建{@link LogicalEndRelationMsg}对象.
     *
     * @param relation 快照所属的关系信息.
     * @return 包含{@code relation}关系信息的{@link LogicalEndRelationMsg}对象.
     * @throws ArgumentNullException 当{@code relation}为{@code null}时抛出.
     */
    public static LogicalEndRelationMsg of(PgReplRelation relation)
    {
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return new LogicalEndRelationMsg(relation);
    }

    /**
     * 快照所属的关系.
     */
    public final PgReplRelation relation;

    /**
     * 构造函数.
     *
     * @param relation 快照所属的关系信息.
     */
    private LogicalEndRelationMsg(PgReplRelation relation)
    {
        this.relation = relation;
    }
}

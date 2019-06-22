/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 表示开始指定关系快照的消息.
 *
 * 当前PostgreSQL流复制协议中并不会产生这个消息。
 * 这个消息是本程序在获取快照时发出的。
 */
public class LogicalBegRelationMsg implements LogicalMsg
{
    /**
     * 根据指定的关系构建{@link LogicalBegRelationMsg}对象.
     *
     * @param relation 快照所属的关系信息.
     * @return 包含{@code relation}关系信息的{@link LogicalBegRelationMsg}对象.
     * @throws ArgumentNullException 当{@code relation}为{@code null}时抛出.
     */
    public static LogicalBegRelationMsg of(PgReplRelation relation)
    {
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        return new LogicalBegRelationMsg(relation);
    }

    /**
     * 快照所属的关系信息.
     */
    public final PgReplRelation relation;

    /**
     * 构造函数.
     *
     * @param relation 快照所属的关系信息.
     */
    private LogicalBegRelationMsg(PgReplRelation relation)
    {
        this.relation = relation;
    }
}

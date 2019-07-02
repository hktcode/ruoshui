/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

/**
 * 开始快照和结束快照的基类.
 */
public abstract class PgsqlValSnapshot extends PgsqlVal
{
    /**
     * 关系列表.
     *
     * 当前快照是该列表中关系的快照.
     */
    public final ImmutableList<PgsqlRelation> relalist;

    /**
     * 构造函数.
     *
     * @param dbserver 服务器唯一标识.
     * @param xidofmsg 消息所在事务编号.
     * @param committs 消息提交的PostgreSQL纪元时间戳.
     * @param relalist 快照中关系的列表.
     */
    protected PgsqlValSnapshot //
        /* */( String dbserver
        /* */, ImmutableList<PgsqlRelation> relalist
        /* */)
    {
        super(dbserver);
        this.relalist = relalist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        super.appendTo(builder);
        builder.append("|");
        char c = '[';
        for (PgsqlRelation name : this.relalist) {
            builder.append(c);
            name.appendTo(builder);
            c = ',';
        }
        builder.append(']');
        return builder.toString();
    }
}

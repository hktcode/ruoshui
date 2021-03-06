/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.entity;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;

/**
 * 在事务中的tuple信息.
 */
public abstract class PgsqlValXidtuple extends PgsqlValTxaction
{
    /**
     * 关系的oid.
     */
    public final long relident;

    /**
     * 关系所在的schema名称.
     */
    public final String dbschema;

    /**
     * 关系名称.
     */
    public final String relation;

    /**
     * 关系的复制标识.
     *
     * 和{@code pg_class}关系中名为{@code relreplident}属性保持一致.
     *
     * 有以下“复制标识（replica identity）”：
     * <dl>
     *   <dt>{@code d}</dt><dd>default (primary key, if any)</dd>
     *   <dt>{@code n}</dt><dd>nothing</dd>
     *   <dt>{@code f}</dt><dd>all columns</dd>
     *   <dt>{@code i}</dt><dd>index with indisreplident set, or default</dd>
     * </dl>
     */
    public final long replchar;

    /**
     * 值列表.
     */
    public final ImmutableList<PgsqlComponent> tupleval;

    /**
     * 构造函数.
     *
     * @param dbserver 服务器唯一标识.
     * @param xidofmsg 消息所在事务编号.
     * @param relident 关系的标识符oid.
     * @param dbschema 关系所在的schema名称.
     * @param relation 关系名称.
     * @param replchar 复制标识.
     * @param tupleval 值列表.
     */
    protected PgsqlValXidtuple //
        /* */( String dbserver //
        /* */, long xidofmsg //
        /* */, long relident //
        /* */, String dbschema //
        /* */, String relation //
        /* */, long replchar //
        /* */, ImmutableList<PgsqlComponent> tupleval //
        /* */, long lsnofmsg //
        /* */)
    {
        super(dbserver, lsnofmsg, xidofmsg);
        this.relident = relident;
        this.dbschema = dbschema;
        this.relation = relation;
        this.replchar = replchar;
        this.tupleval = tupleval;
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        super.appendTo(builder);
        builder.append('|');
        builder.append(relident);
        builder.append('|');
        builder.append(dbschema);
        builder.append('|');
        builder.append(relation);
        builder.append('|');
        builder.append((char)replchar);
        for(PgsqlComponent component : tupleval) {
            builder.append("\n    ");
            component.appendTo(builder);
        }
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node = super.toJsonObject(node);
        node.put("relident", relident);
        node.put("dbschema", dbschema);
        node.put("relation", relation);
        node.put("replchar", replchar);
        ArrayNode array = node.putArray("tupleval");
        for (PgsqlComponent component: tupleval) {
            ObjectNode n = array.addObject();
            component.putTo(n);
        }
        return node;
    }
}

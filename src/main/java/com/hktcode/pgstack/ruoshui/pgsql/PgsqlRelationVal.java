/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

/**
 * 含有一个关系的快照消息的基类.
 */
public abstract class PgsqlRelationVal extends PgsqlVal
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
     * 关系的属性列表.
     */
    public final ImmutableList<PgsqlAttribute> attrlist;

    /**
     * 构造函数.
     *
     * @param dbserver 服务器唯一标识.
     * @param xidofmsg 消息所在事务编号.
     * @param committs 消息提交的PostgreSQL纪元时间戳.
     * @param relident 关系的标识符oid.
     * @param dbschema 关系所在的schema名称.
     * @param relation 关系名称.
     * @param replchar 复制标识.
     * @param attrlist 属性列表.
     */
    protected PgsqlRelationVal //
        /* */( String dbserver //
        /* */, long relident //
        /* */, String dbschema //
        /* */, String relation //
        /* */, long replchar //
        /* */, ImmutableList<PgsqlAttribute> attrlist //
        /* */)
    {
        super(dbserver);
        this.relident = relident;
        this.dbschema = dbschema;
        this.relation = relation;
        this.replchar = replchar;
        this.attrlist = attrlist;
    }

    /**
     * 将关系的元数据附加到指定的{@code StringBuilder}后面.
     *
     * 一般用于生成{@link #toString()}人类可读信息.
     *
     * @param builder 指定的{@code StringBuilder}对象.
     */
    protected void appendMetadataTo(StringBuilder builder)
    {
        builder.append(dbschema);
        builder.append('|');
        builder.append(relation);
        builder.append('|');
        builder.append(relident);
        builder.append('|');
        builder.append((char)replchar);
    }

    /**
     * 将关系的属性数据附加到指定的{@code StringBuilder}后面.
     *
     * 一般用于生成{@link #toString()}人类可读信息.
     *
     * @param builder 指定的{@code StringBuilder}对象.
     */
    public void appendAttrlistTo(StringBuilder builder)
    {
        for(PgsqlAttribute attr: attrlist) {
            builder.append("\n    ");
            attr.appendTo(builder);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        super.appendTo(builder);
        builder.append('|');
        this.appendMetadataTo(builder);
        this.appendAttrlistTo(builder);
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectNode toObjectNode()
    {
        ObjectNode node = super.toObjectNode();
        node.put("relident", this.relident);
        node.put("dbschema", this.dbschema);
        node.put("relation", this.relation);
        node.put("replchar", this.replchar);
        ArrayNode array = node.putArray("attrlist");
        for (PgsqlAttribute attribute : attrlist) {
            ObjectNode n = array.addObject();
            attribute.putTo(n);
        }
        return node;
    }
}

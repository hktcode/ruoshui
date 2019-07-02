/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.PgReplAttribute;
import com.hktcode.pgjdbc.PgReplRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示PostgreSQL中的关系.
 */
public class PgsqlRelation
{
    /**
     * 根据{@link PgReplRelation}构建{@link PgsqlRelation}对象.
     *
     * @param relation 用于构建{@link PgsqlRelation}的{@link PgReplRelation}对象.
     * @return 根据{@code relation}构建的{@link PgsqlRelation}对象.
     * @throws ArgumentNullException if {@code relation} is {@code null}.
     */
    public static PgsqlRelation of(PgReplRelation relation)
    {
        if (relation == null) {
            throw new ArgumentNullException("relation");
        }
        List<PgsqlAttribute> attrlist = new ArrayList<>();
        for (PgReplAttribute attribute : relation.attrlist) {
            PgsqlAttribute attr = PgsqlAttribute.of(attribute);
            attrlist.add(attr);
        }
        return new PgsqlRelation //
            /* */( relation.relident //
            /* */, relation.dbschema //
            /* */, relation.relation //
            /* */, relation.replchar //
            /* */, ImmutableList.copyOf(attrlist) //
            /* */);
    }

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
     * @param relident 关系的标识符oid.
     * @param dbschema 关系所在的schema名称.
     * @param relation 关系名称.
     * @param replchar 复制标识.
     * @param attrlist 属性列表.
     */
    private PgsqlRelation //
    /* */( long relident //
        /* */, String dbschema //
        /* */, String relation //
        /* */, long replchar //
        /* */, ImmutableList<PgsqlAttribute> attrlist //
        /* */)
    {
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
     * @throws ArgumentNullException if {@code builder} is {@code null}.
     */
    public void appendTo(StringBuilder builder)
    {
        if (builder == null) {
            throw new ArgumentNullException("builder");
        }
        builder.append(dbschema);
        builder.append('|');
        builder.append(relation);
        builder.append('|');
        builder.append(relident);
        builder.append('|');
        builder.append((char)replchar);
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
        this.appendTo(builder);
        return builder.toString();
    }

    /**
     * 将本关系的信息放入{@code ObjectNode}对象中.
     *
     * @param node 存放信息的{@link ObjectNode}对象.
     * @throws ArgumentNullException if {@code node} is {@code null}.
     */
    public void putTo(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put("relident", this.relident);
        node.put("dbschema", this.dbschema);
        node.put("relation", this.relation);
        node.put("replchar", this.replchar);
        ArrayNode array = node.putArray("attrlist");
        for (PgsqlAttribute attribute : attrlist) {
            ObjectNode n = array.addObject();
            attribute.putTo(n);
        }
    }
}

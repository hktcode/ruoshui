/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalCreateTupleMsg;
import com.hktcode.pgjdbc.PgReplComponent;
import com.hktcode.pgjdbc.PgReplRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * 在快照中的tuple信息.
 */
public class PgsqlValTupleCreate extends PgsqlVal
{
    /**
     * 根据快照消息流中的tuple创建消息和复制上下文构建{@link PgsqlValTupleCreate}对象.
     *
     * @param msg 快照消息流中的快照开始消息.
     * @param ctx 逻辑复制上下文.
     *
     * @return 根据快照消息流中的tuple创建消息和复制上下文构建的{@link PgsqlValTupleCreate}对象.
     * @throws ArgumentNullException if {@code ctx} is {@code null}.
     */
    public static ImmutableList<PgsqlVal>
    of(LogicalCreateTupleMsg msg, LogicalTxactContext ctx)
    {
        if (msg == null) {
            throw new ArgumentNullException("lsn");
        }
        if (ctx == null) {
            throw new ArgumentNullException("ctx");
        }
        PgReplRelation relation = msg.relation;
        List<PgsqlComponentThe> tupleval = new ArrayList<>();
        for (int i = 0; i < msg.tupleval.size(); ++i) {
            PgReplComponent component = msg.tupleval.get(i);
            PgsqlComponentThe thevalue = PgsqlComponentThe.of
                /* */( component.attrinfo.attrname //
                /* */, component.attrinfo.tpschema //
                /* */, component.attrinfo.typename //
                /* */, component.attrinfo.datatype //
                /* */, component.attrinfo.attypmod //
                /* */, component.attrinfo.attflags //
                /* */, component.newvalue //
                /* */);
            tupleval.add(thevalue);
        }

        PgsqlValTupleCreate val = new PgsqlValTupleCreate //
            /* */( ctx.dbserver //
            /* */, relation.relident //
            /* */, relation.dbschema //
            /* */, relation.relation //
            /* */, relation.replchar //
            /* */, ImmutableList.copyOf(tupleval) //
            /* */);
        return ImmutableList.of(val);
    }

    /**
     * 类型协议号.
     */
    public static final long PROTOCOL = 1L;

    /**
     * 类型的名称.
     */
    public static final String TYPENAME = "PgsqlTupleCreate";

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
    public final ImmutableList<PgsqlComponentThe> tupleval;

    /**
     * 构造函数.
     *
     * @param dbserver 服务器唯一标识.
     * @param relident 关系的标识符oid.
     * @param dbschema 关系所在的schema名称.
     * @param relation 关系名称.
     * @param replchar 复制标识.
     * @param tupleval 值列表.
     */
    private PgsqlValTupleCreate //
        /* */( String dbserver //
        /* */, long relident //
        /* */, String dbschema //
        /* */, String relation //
        /* */, long replchar //
        /* */, ImmutableList<PgsqlComponentThe> tupleval //
        /* */)
    {
        super(dbserver);
        this.relident = relident;
        this.dbschema = dbschema;
        this.relation = relation;
        this.replchar = replchar;
        this.tupleval = tupleval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectNode toObjectNode()
    {
        ObjectNode node = super.toObjectNode();
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

    /**
     * {@inheritDoc}
     *
     * @see #PROTOCOL
     */
    @Override
    public long getProtocol()
    {
        return PgsqlValTupleCreate.PROTOCOL;
    }

    /**
     * {@inheritDoc}
     *
     * @see #TYPENAME
     */
    @Override
    public String getTypename()
    {
        return PgsqlValTupleCreate.TYPENAME;
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
        for(PgsqlComponentThe component : tupleval) {
            builder.append("\n    ");
            component.appendTo(builder);
        }
        return builder.toString();
    }
}

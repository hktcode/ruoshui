/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.entity;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalRelTruncateMsg;
import com.hktcode.pgjdbc.PgReplAttribute;
import com.hktcode.pgjdbc.PgReplRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * 截断关系的消息.
 */
public class PgsqlValTruncateRel extends PgsqlValTxaction
{
    /**
     * 根据提交LSN、逻辑复制流中的截断关系消息和逻辑复制上下文构建{@link PgsqlValTruncateRel}对象.
     *
     * @param lsn 该消息在wal中的位置.
     * @param msg 逻辑复制流中的截断关系消息.
     * @param ctx 逻辑复制上下文.
     *
     * @return 根据提交LSN、逻辑复制流中的事务消息和逻辑复制上下文构建的{@link PgsqlValTruncateRel}对象列表.
     * @throws ArgumentNullException if {@code msg} or {@code ctx} is {@code null}.
     */
    public static ImmutableList<PgsqlVal>
    of(long lsn, LogicalRelTruncateMsg msg, LogicalTxactContext ctx)
    {
        if (msg == null) {
            throw new ArgumentNullException("lsn");
        }
        if (ctx == null) {
            throw new ArgumentNullException("ctx");
        }
        List<PgsqlVal> result = new ArrayList<>();
        for (int i = 0; i < msg.relalist.length(); ++i) {
            long relident = msg.relalist.get(i);
            PgReplRelation relation = ctx.relalist.get(relident);
            if (relation == null) {
                throw new RuntimeException(); // TODO:
            }
            List<PgsqlAttribute> attrlist = new ArrayList<>();
            for (PgReplAttribute attrinfo : relation.attrlist) {
                attrlist.add(PgsqlAttribute.of(attrinfo));
            }
            PgsqlValTruncateRel val = new PgsqlValTruncateRel
                /* */( ctx.dbserver
                /* */, ctx.xidofmsg
                /* */, ctx.committs
                /* */, relident
                /* */, relation.dbschema
                /* */, relation.relation
                /* */, relation.replchar
                /* */, ImmutableList.copyOf(attrlist)
                /* */, msg.optionbs
                /* */, lsn
                /* */);
                result.add(val);
        }
        return ImmutableList.copyOf(result);
    }

    /**
     * 类型协议号.
     */
    public static final long PROTOCOL = 4L;

    /**
     * 类型的名称.
     */
    public static final String TYPENAME = "PgsqlTruncateRel";

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
     * TRUNCATE语句的选项位信息.
     *
     * <dl>
     *     <dt>{@code 1}</dt><dd>for CASCADE</dd>
     *     <dt>{@code 2}</dt><dd>for RESTART IDENTITY</dd>
     * </dl>
     */
    public final long optionbs;

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
     * @param optionbs 语句选项.
     */
    private PgsqlValTruncateRel //
        /* */( String dbserver //
        /* */, long xidofmsg //
        /* */, long committs //
        /* */, long relident //
        /* */, String dbschema //
        /* */, String relation //
        /* */, long replchar //
        /* */, ImmutableList<PgsqlAttribute> attrlist //
        /* */, long optionbs //
        /* */, long lsnofmsg //
        /* */)
    {
        super(dbserver, lsnofmsg, xidofmsg, committs);
        this.relident = relident;
        this.dbschema = dbschema;
        this.relation = relation;
        this.replchar = replchar;
        this.attrlist = attrlist;
        this.optionbs = optionbs;
    }

    /**
     * {@inheritDoc}
     *
     * @see #PROTOCOL
     */
    @Override
    public long getProtocol()
    {
        return PgsqlValTruncateRel.PROTOCOL;
    }

    /**
     * {@inheritDoc}
     *
     * @see #TYPENAME
     */
    @Override
    public String getTypename()
    {
        return PgsqlValTruncateRel.TYPENAME;
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
        builder.append(dbschema);
        builder.append('|');
        builder.append(relation);
        builder.append('|');
        builder.append(relident);
        builder.append('|');
        builder.append((char)replchar);
        builder.append('|');
        builder.append(this.optionbs);
        for(PgsqlAttribute attr: attrlist) {
            builder.append("\n    ");
            attr.appendTo(builder);
        }
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
        node.put("optionbs", this.optionbs);
        return node;
    }
}

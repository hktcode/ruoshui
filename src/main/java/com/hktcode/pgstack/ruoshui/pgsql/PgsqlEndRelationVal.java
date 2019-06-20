/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndRelationMsg;
import com.hktcode.pgjdbc.PgReplAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * 某个关系快照结束消息.
 */
public class PgsqlEndRelationVal extends PgsqlRelationVal
{
    /**
     * 根据快照消息流中的某个关系快照的结束消息和复制上下文构建{@link PgsqlEndRelationVal}对象.
     *
     * @param msg 快照消息流中的某个关系快照的结束消息.
     * @param ctx 逻辑复制上下文.
     *
     * @return 根据快照消息流中的某个关系快照结束消息和复制上下文构建的{@link PgsqlEndRelationVal}对象.
     * @throws ArgumentNullException if {@code ctx} is {@code null}.
     */
    public static ImmutableList<PgsqlVal> //
    of(LogicalEndRelationMsg msg, LogicalTxactContext ctx)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        if (ctx == null) {
            throw new ArgumentNullException("ctx");
        }
        List<PgsqlAttribute> attrlist = new ArrayList<>();
        for(PgReplAttribute attrinfo : msg.relation.attrlist) {
            attrlist.add(PgsqlAttribute.of(attrinfo));
        }
        PgsqlEndRelationVal val = new PgsqlEndRelationVal //
            ( ctx.dbserver //
                , msg.relation.relident //
                , msg.relation.dbschema //
                , msg.relation.relation //
                , msg.relation.replchar //
                , ImmutableList.copyOf(attrlist) //
            );
        return ImmutableList.of(val);
    }

    /**
     * 类型协议号.
     */
    public static final long PROTOCOL = 3L;

    /**
     * 类型的名称.
     */
    public static final String TYPENAME = "PgsqlEndRelation";

    /**
     * 构造函数.
     *
     * @param dbserver 服务器唯一标识.
     * @param relident 关系的标识符oid.
     * @param dbschema 关系所在的schema名称.
     * @param relation 关系名称.
     * @param replchar 复制标识.
     * @param attrlist 属性列表.
     */
    private PgsqlEndRelationVal //
        /* */(String dbserver //
        /* */, long relident //
        /* */, String dbschema //
        /* */, String relation //
        /* */, long replchar //
        /* */, ImmutableList<PgsqlAttribute> attrlist //
        /* */)
    {
        super(dbserver //
            , relident //
            , dbschema //
            , relation //
            , replchar //
            , attrlist //
        );
    }

    /**
     * {@inheritDoc}
     *
     * @see #PROTOCOL
     */
    @Override
    public long getProtocol()
    {
        return PgsqlEndRelationVal.PROTOCOL;
    }

    /**
     * {@inheritDoc}
     *
     * @see #TYPENAME
     */
    @Override
    public String getTypename()
    {
        return PgsqlEndRelationVal.TYPENAME;
    }
}

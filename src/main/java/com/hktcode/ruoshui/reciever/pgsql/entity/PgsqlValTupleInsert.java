/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalTupleInsertMsg;
import com.hktcode.pgjdbc.PgReplAttribute;
import com.hktcode.pgjdbc.PgReplRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code INSERT}消息.
 */
public class PgsqlValTupleInsert extends PgsqlValXidtuple
{
    /**
     * 根据提交LSN、逻辑复制流中的{@code INSERT}消息和逻辑复制上下文构建{@link PgsqlValTupleInsert}对象.
     *
     * @param lsn 该消息在wal中的位置.
     * @param msg 逻辑复制流中的{@code INSERT}消息.
     * @param ctx 逻辑复制上下文.
     *
     * @return 根据提交LSN、逻辑复制流中的事务消息和逻辑复制上下文构建的{@link PgsqlValTupleInsert}对象.
     * @throws ArgumentNullException if {@code msg} or {@code ctx} is {@code null}.
     */
    public static ImmutableList<PgsqlVal>
    of(long lsnofmsg, LogicalTupleInsertMsg msg, LogicalTxactContext ctx)
    {
        if (msg == null) {
            throw new ArgumentNullException("lsn");
        }
        if (ctx == null) {
            throw new ArgumentNullException("ctx");
        }
        PgReplRelation relation = ctx.relalist.get(msg.relident);
        if (relation == null) {
            throw new RuntimeException(); // TODO:
        }
        List<PgsqlComponent> tupleval = new ArrayList<>();
        for (int i = 0; i < relation.attrlist.size(); ++i) {
            PgReplAttribute attrinfo = relation.attrlist.get(i);
            JsonNode oldvalue = MissingNode.getInstance();
            JsonNode newvalue ;
            if (i < msg.tupleval.size()) {
                newvalue = msg.tupleval.get(i);
            }
            else {
                newvalue = MissingNode.getInstance();
            }
            tupleval.add(PgsqlComponentAll.of(attrinfo, oldvalue, newvalue));
        }

        PgsqlValTupleInsert val = new PgsqlValTupleInsert//
            /* */( ctx.dbserver //
            /* */, ctx.xidofmsg //
            /* */, msg.relident //
            /* */, relation.dbschema //
            /* */, relation.relation //
            /* */, relation.replchar //
            /* */, ImmutableList.copyOf(tupleval) //
            /* */, lsnofmsg //
            /* */);
        return ImmutableList.of(val);
    }

    /**
     * 类型协议号.
     */
    public static final long PROTOCOL = 5L;

    /**
     * 类型的名称.
     */
    public static final String TYPENAME = "PgsqlTupleInsert";

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
     * @param tupleval 值列表.
     */
    private PgsqlValTupleInsert //
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
        super(dbserver //
            , xidofmsg //
            , relident //
            , dbschema //
            , relation //
            , replchar //
            , tupleval //
            , lsnofmsg //
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
        return PgsqlValTupleInsert.PROTOCOL;
    }

    /**
     * {@inheritDoc}
     *
     * @see #TYPENAME
     */
    @Override
    public String getTypename()
    {
        return PgsqlValTupleInsert.TYPENAME;
    }
}

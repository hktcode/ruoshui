/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.*;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code UPDATE}消息.
 */
public class PgsqlValTupleUpdate extends PgsqlValXidtuple
{
    /**
     * 根据提交LSN、逻辑复制流中的{@code UPDATE}消息和逻辑复制上下文构建{@link PgsqlValTupleUpdate}对象.
     *
     * @param lsn 该消息在wal中的位置.
     * @param msg 逻辑复制流中的{@code UPDATE}消息.
     * @param ctx 逻辑复制上下文.
     *
     * @return 根据提交LSN、逻辑复制流中的事务消息和逻辑复制上下文构建的{@link PgsqlValTupleUpdate}对象.
     * @throws ArgumentNullException if {@code msg} or {@code ctx} is {@code null}.
     */
    public static ImmutableList<PgsqlVal>
    of(long lsn, LogicalTupleUpdateMsg msg, LogicalTxactContext ctx)
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
            if (msg instanceof LogicalKeyTupleUpdateMsg) {
                LogicalKeyTupleUpdateMsg key = (LogicalKeyTupleUpdateMsg)msg;
                if (attrinfo.attflags == 1) {
                    oldvalue = key.keytuple.get(i);
                }
            }
            else if (msg instanceof LogicalOldTupleUpdateMsg) {
                LogicalOldTupleUpdateMsg old = (LogicalOldTupleUpdateMsg)msg;
                if (i < old.oldtuple.size()) {
                    oldvalue = old.oldtuple.get(i);
                }
            }
            JsonNode newvalue = MissingNode.getInstance();
            if (i < msg.newtuple.size()) {
                newvalue = msg.newtuple.get(i);
            }
            tupleval.add(PgsqlComponentAll.of(attrinfo, oldvalue, newvalue));
        }

        PgsqlValTupleUpdate val = new PgsqlValTupleUpdate//
            /* */( ctx.dbserver //
            /* */, ctx.xidofmsg //
            /* */, ctx.committs //
            /* */, msg.relident //
            /* */, relation.dbschema //
            /* */, relation.relation //
            /* */, relation.replchar //
            /* */, ImmutableList.copyOf(tupleval) //
            /* */, lsn //
            /* */);
        return ImmutableList.of(val);
    }

    /**
     * 类型协议号.
     */
    public static final long PROTOCOL = 7L;

    /**
     * 类型的名称.
     */
    public static final String TYPENAME = "PgsqlTupleUpdate";

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
    private PgsqlValTupleUpdate //
        /* */( String dbserver //
        /* */, long xidofmsg //
        /* */, long committs //
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
            , committs //
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
        return PgsqlValTupleUpdate.PROTOCOL;
    }

    /**
     * {@inheritDoc}
     *
     * @see #TYPENAME
     */
    @Override
    public String getTypename()
    {
        return PgsqlValTupleUpdate.TYPENAME;
    }
}

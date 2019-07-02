/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalEndSnapshotMsg;
import com.hktcode.pgjdbc.PgReplRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * 快照结束消息.
 */
public class PgsqlValSnapshotEnd extends PgsqlValSnapshot
{
    /**
     * 根据快照消息流中的快照结束消息和复制上下文构建{@link PgsqlValSnapshotEnd}对象.
     *
     * @param msg 快照消息流中的快照开始消息.
     * @param ctx 逻辑复制上下文.
     *
     * @return 根据快照消息流中的快照结束消息和复制上下文构建的{@link PgsqlValSnapshotEnd}对象.
     * @throws ArgumentNullException if {@code ctx} is {@code null}.
     */
    public static ImmutableList<PgsqlVal> //
    of(LogicalEndSnapshotMsg msg, LogicalTxactContext ctx)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        if (ctx == null) {
            throw new ArgumentNullException("ctx");
        }
        List<PgsqlRelation> relalist = new ArrayList<>();
        for (PgReplRelation relation : msg.relalist) {
            PgsqlRelation rela = PgsqlRelation.of(relation);
            relalist.add(rela);
        }
        PgsqlVal val = new PgsqlValSnapshotEnd //
            /* */( ctx.dbserver //
            /* */, ImmutableList.copyOf(relalist) //
            /* */);
        return ImmutableList.of(val);
    }

    /**
     * 该类型协议号.
     */
    public static final long PROTOCOL = 11L;

    /**
     * 该类型的名称.
     */
    public static final String TYPENAME = "PgsqlSnapshotEnd";

    /**
     * 构造函数.
     *
     * @param dbserver 服务器唯一标识.
     * @param relalist 快照中关系的列表.
     */
    private PgsqlValSnapshotEnd //
        /* */( String dbserver
        /* */, ImmutableList<PgsqlRelation> relalist
        /* */)
    {
        super(dbserver, relalist);
    }

    /**
     * {@inheritDoc}
     *
     * @see #PROTOCOL
     */
    @Override
    public long getProtocol()
    {
        return PgsqlValSnapshotEnd.PROTOCOL;
    }

    /**
     * {@inheritDoc}
     *
     * @see #TYPENAME
     */
    @Override
    public String getTypename()
    {
        return PgsqlValSnapshotEnd.TYPENAME;
    }
}

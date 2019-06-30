/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.pgsql;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PostgreSQL写入外部消息队列的值.
 */
public abstract class PgsqlVal
{
    private static final Logger logger = LoggerFactory.getLogger(PgsqlVal.class);

    /**
     * 根据提交LSN、逻辑复制流消息和逻辑复制上下文构建{@link PgsqlVal}对象.
     *
     * @param lsn 如果是事务消息，则是在该消息在wal中的位置，如果是快照消息，则是一致点的LSN（实际中并未使用）.
     * @param msg 逻辑复制流消息.
     * @param ctx 逻辑复制上下文.
     *
     * @return 根据提交LSN、逻辑复制流消息和逻辑复制上下文构建的{@link PgsqlVal}对象.
     * @throws ArgumentNullException if {@code msg} or {@code ctx} is {@code null}.
     */
    public static ImmutableList<PgsqlVal> //
    of(long lsn, LogicalMsg msg, LogicalTxactContext ctx)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        if (ctx == null) {
            throw new ArgumentNullException("ctx");
        }
        if (msg instanceof LogicalNoOperationMsg) {
            LogicalNoOperationMsg val = (LogicalNoOperationMsg)msg;
            return PgsqlValNoOperation.of(val, ctx);
        }
        else if (msg instanceof LogicalBegSnapshotMsg) {
            LogicalBegSnapshotMsg val = (LogicalBegSnapshotMsg)msg;
            return PgsqlValSnapshotBeg.of(val, ctx);
        }
        else if (msg instanceof LogicalEndSnapshotMsg) {
            LogicalEndSnapshotMsg val = (LogicalEndSnapshotMsg)msg;
            return PgsqlValSnapshotEnd.of(val, ctx);
        }
        else if (msg instanceof LogicalTxactBeginsMsg) {
            LogicalTxactBeginsMsg val = (LogicalTxactBeginsMsg)msg;
            return PgsqlValTxactBegins.of(lsn, val, ctx);
        }
        else if (msg instanceof LogicalTxactCommitMsg) {
            LogicalTxactCommitMsg val = (LogicalTxactCommitMsg) msg;
            return PgsqlValTxactCommit.of(lsn, val, ctx);
        }
        else if (msg instanceof LogicalBegRelationMsg) {
            LogicalBegRelationMsg val = (LogicalBegRelationMsg) msg;
            return PgsqlValRelationBeg.of(val, ctx);
        }
        else if (msg instanceof LogicalEndRelationMsg) {
            LogicalEndRelationMsg val = (LogicalEndRelationMsg)msg;
            return PgsqlValRelationEnd.of(val, ctx);
        }
        else if (msg instanceof LogicalRelTruncateMsg) {
            LogicalRelTruncateMsg val = (LogicalRelTruncateMsg)msg;
            return PgsqlValTruncateRel.of(lsn, val, ctx);
        }
        else if (msg instanceof LogicalTupleInsertMsg) {
            LogicalTupleInsertMsg val = (LogicalTupleInsertMsg)msg;
            return PgsqlValTupleInsert.of(lsn, val, ctx);
        }
        else if (msg instanceof LogicalTupleUpdateMsg) {
            LogicalTupleUpdateMsg val = (LogicalTupleUpdateMsg)msg;
            return PgsqlValTupleUpdate.of(lsn, val, ctx);
        }
        else if (msg instanceof LogicalTupleDeleteMsg) {
            LogicalTupleDeleteMsg val = (LogicalTupleDeleteMsg)msg;
            return PgsqlValTupleDelete.of(lsn, val, ctx);
        }
        else if (msg instanceof LogicalCreateTupleMsg) {
            LogicalCreateTupleMsg val = (LogicalCreateTupleMsg)msg;
            return PgsqlValTupleCreate.of(val, ctx);
        }
        else if (msg instanceof LogicalRelationInfMsg) {
            LogicalRelationInfMsg val = (LogicalRelationInfMsg)msg;
            ctx.putRelation(val);
            return ImmutableList.of();
        }
        else if (msg instanceof LogicalDatatypeInfMsg) {
            LogicalDatatypeInfMsg val = (LogicalDatatypeInfMsg)msg;
            ctx.typelist.put(val.datatype, val);
            return ImmutableList.of();
        }
        else if (msg instanceof LogicalOriginNamesMsg) {
            logger.info("ignore LogicalOriginNamesMsg: msg={}", msg);
            return ImmutableList.of();
        }
        else {
            throw new RuntimeException(); // TODO:
        }
    }

    /**
     * 服务器地址.
     *
     * 用于唯一标识一个机器地址，如果是空字符串，表示机器地址丢失。
     * 可考虑用服务名、IP地址、域名和端口来作为此字段的值.
     *
     * TODO: 像{@code protocol}、{@code typename}一样定义一个成员变量，还是像现在这样定义成函数，还需要考虑
     */
    public final String dbserver;

    /**
     * 类型协议号.
     *
     * TODO: 像{@code dbserver} 一样定义一个成员变量，还是像现在这样定义成函数，还需要考虑
     * @return 非负数的一个long值，用于唯一标识一个类型.
     */
    public abstract long getProtocol();

    /**
     * 类型的名称.
     *
     * TODO: 像{@link dbserver} 一样定义一个成员变量，还是像现在这样定义成函数，还需要考虑
     * @return 类型名称.
     */
    public abstract String getTypename();

    /**
     * 构造函数.
     *
     * @param dbserver 服务器唯一标识.
     */
    protected PgsqlVal(String dbserver)
    {
        this.dbserver = dbserver;
    }

    /**
     * 将{@code PgsqlVal}特性形式的字符串添加到{@code builder}后面.
     *
     * 一般用于生成{@link #toString()}字符串中人类可读的形式.
     *
     * @param builder 要添加的{@code StringBuilder}对象.
     */
    protected void appendTo(StringBuilder builder)
    {
        builder.append(this.getTypename());
        builder.append('|');
        builder.append(this.getProtocol());
        builder.append('|');
        builder.append(this.dbserver);
    }

    /**
     * 转换成{@link ObjectNode}形式.
     *
     * @return {@link ObjectNode}对象.
     */
    public ObjectNode toObjectNode()
    {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.put("protocol", this.getProtocol());
        node.put("typename", this.getTypename());
        node.put("dbserver", this.dbserver);
        return node;
    }
}

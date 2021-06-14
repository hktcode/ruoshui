/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.postgresql.replication.fluent.logical.ChainedLogicalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.hktcode.pgjdbc.LogicalStreamStarter.DEFAULT_START_POSITION;
import static com.hktcode.pgjdbc.LogicalStreamStarter.DEFAULT_STATUS_INTERVAL;
import static com.hktcode.ruoshui.Ruoshui.THE_NAME;

/**
 * PostgreSQL逻辑复制流配置类.
 */
public class LogicalReplArgval implements JacksonObject
{
    private static final Logger logger = LoggerFactory.getLogger(LogicalReplArgval.class);

    /**
     * 默认的复制槽名称.
     */
    private static final String DEFAULT_SLOT_NAME = THE_NAME;

    /**
     * 默认的发布名称.
     */
    private static final String DEFAULT_PUBLICATION_NAME = THE_NAME;

    /**
     * 根据{@link JsonNode}构建{@link LogicalReplArgval}.
     *
     * @param node {@link JsonNode}对象.
     *
     * @return 根据{@code node}构建的{@link LogicalReplArgval}对象.
     * @throws ArgumentNullException if {@code node} is {@code null}.
     */
    public static LogicalReplArgval of(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        String s = node.path("slot_name").asText(DEFAULT_SLOT_NAME);
        int i = node.path("status_interval").asInt(DEFAULT_STATUS_INTERVAL);
        long p = node.path("start_position").asLong(DEFAULT_START_POSITION);
        ArrayNode publicationNames = new ArrayNode(JsonNodeFactory.instance);
        publicationNames.add(DEFAULT_PUBLICATION_NAME);
        JsonNode publicationNamesNode = node.get("publication_names");
        if (publicationNamesNode instanceof ArrayNode) {
            ArrayNode arr = (ArrayNode)publicationNamesNode;
            if (arr.size() > 0) {
                publicationNames = arr;
            }
        }
        String[] list = new String[publicationNames.size()];
        for (int j = 0; j < list.length; ++j) {
            list[j] = publicationNames.get(j).asText();
        }
        ImmutableList<String> n = ImmutableList.copyOf(list);
        return new LogicalReplArgval(s, i, p, n);
    }

    /**
     * 复制槽名称.
     */
    public final String slotName;

    /**
     * 向PostgreSQL服务器发送状态信息的时间间隔.
     *
     * @see <a href="https://www.postgresql.org/docs/10/runtime-config-replication.html#RUNTIME-CONFIG-REPLICATION-STANDBY">PostgreSQL 10官方文档</a>
     */
    public final int statusInterval;

    /**
     * 复制槽开始位置.
     *
     * 如果是{@code 0}，表示接收流复制信息时不设置开始位置信息。
     */
    public final long startPosition;

    /**
     * PostgreSQL服务端的发布名称列表.
     *
     * @see <a href="https://www.postgresql.org/docs/10/protocol-replication.html">PostgreSQL 10官方文档</a>
     */
    public final ImmutableList<String> publicationNames;

    /**
     * 构造函数.
     *
     * @param slotName 复制槽名称.
     * @param statusInterval 向primary服务器报告状态信息的时间间隔.
     * @param startPosition 复制槽开始位置.
     * @param publicationNames 发布名称列表.
     */
    private LogicalReplArgval //
        /* */( String slotName
        /* */, int statusInterval
        /* */, long startPosition
        /* */, ImmutableList<String> publicationNames
        /* */)
    {
        this.slotName = slotName;
        this.statusInterval = statusInterval;
        this.startPosition = startPosition;
        this.publicationNames = publicationNames;
    }

    /**
     * 启动PostgreSQL复制流.
     *
     * @param pgc PostgreSQL连接.
     *
     * @return 表示PostgreSQL逻辑复制流的{@link PGReplicationStream}对象.
     *
     * @throws SQLException 当启动复制流出现错误时抛出.
     */
    public PGReplicationStream start(PgConnection pgc) throws SQLException
    {
        StringBuilder sb = new StringBuilder(this.publicationNames.get(0));
        for (int i = 1; i < this.publicationNames.size(); ++i) {
            sb.append(pgc.escapeIdentifier(this.publicationNames.get(i)));
            sb.append(',');
        }
        Properties properties = new Properties();
        properties.setProperty("proto_version", "1");
        properties.setProperty("publication_names", sb.toString());
        ChainedLogicalStreamBuilder streamBuilder //
            = pgc.getReplicationAPI()
            .replicationStream()
            .logical()
            .withSlotName(this.slotName)
            .withStatusInterval(this.statusInterval, TimeUnit.MILLISECONDS)
            .withSlotOptions(properties);
        if (LogSequenceNumber.INVALID_LSN.asLong() != this.startPosition) {
            LogSequenceNumber pos = LogSequenceNumber.valueOf(this.startPosition);
            streamBuilder = streamBuilder.withStartPosition(pos);
        }
        logger.info("start logical stream: logicalSlot={}", this);
        return streamBuilder.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("slotName=");
        sb.append(this.slotName);
        sb.append(", statusInterval=");
        sb.append(this.statusInterval);
        sb.append(", startPosition=");
        sb.append(LogSequenceNumber.valueOf(this.startPosition).asString());
        sb.append(", publicationNames=");
        sb.append(this.publicationNames);
        return sb.toString();
    }

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put("slot_name", this.slotName);
        LogSequenceNumber start = LogSequenceNumber.valueOf(this.startPosition);
        node.put("start_position", start.asString());
        ArrayNode names = node.putArray("publication_names");
        for (String name : this.publicationNames) {
            names.add(name);
        }
        return node;
    }
}

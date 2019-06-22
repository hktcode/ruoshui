/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgjdbc;

import com.google.common.collect.ImmutableMap;
import com.hktcode.lang.exception.ArgumentNegativeException;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.postgresql.replication.fluent.logical.ChainedLogicalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

/**
 * PostgreSQL逻辑复制流的启动器类.
 */
public class LogicalStreamStarter
{
    private static final Logger logger = LoggerFactory.getLogger(LogicalStreamStarter.class);

    /**
     * {@code LogicalStreamStarter}成员变量{@code statusInterval}的建议默认值.
     *
     * <p>
     * 采用该默认值的原因是PostgreSQL 10中官方文档中定义的默认值也是该值.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/10/runtime-config-replication.html#RUNTIME-CONFIG-REPLICATION-STANDBY">PostgreSQL 10官方文档</a>
     */
    public static final int DEFAULT_STATUS_INTERVAL = 10 * 1000;

    /**
     * {@code LogicalStreamStarter}成员变量{@code startPosition}的建议默认值.
     *
     * <p>
     * 默认值0表示启动逻辑复制流时不指定起始位置.
     * </p>
     *
     * @see #startPosition
     */
    public static final long DEFAULT_START_POSITION = 0;

    /**
     * 构造一个{@code LogicalStreamStarter}.
     *
     * @param slotName 复制槽名称.
     * @param statusInterval 向PostgreSQL服务器报告状态的时间间隔.
     * @param startPosition 复制槽的开始位置.
     * @param options 复制槽选项.
     *
     * @return 构造的LogicalSlotParams对象.
     *
     * @throws ArgumentNullException 当参数{@code slotName}或者{@code options}为{@code null}时抛出.
     * @throws SlotNameIllegalException 当参数{@code slotName}不为{@code null}且不匹配{@link PostgreSQL#slotNamePattern}时抛出.
     */
    public static LogicalStreamStarter of //
        /* */( String slotName //
        /* */, int statusInterval //
        /* */, long startPosition //
        /* */, ImmutableMap<String, String> options //
        /* */) //
    {
        if (slotName == null) {
            throw new ArgumentNullException("slotName");
        }
        if (options == null) {
            throw new ArgumentNullException("options");
        }
        Matcher matcher = PostgreSQL.slotNamePattern.matcher(slotName);
        if (!matcher.matches()) {
            throw new SlotNameIllegalException(slotName);
        }
        if (statusInterval < 0) {
            throw new ArgumentNegativeException("statusInterval", statusInterval);
        }
        return new LogicalStreamStarter(slotName, statusInterval, startPosition, options);
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
     * <p>
     * 如果是{@code 0}，表示接收流复制信息时不设置开始位置信息。
     * </p>
     */
    public final long startPosition;

    /**
     * 复制槽选项信息.
     *
     * @see <a href="https://www.postgresql.org/docs/10/protocol-replication.html">PostgreSQL 10官方文档</a>
     */
    public final ImmutableMap<String, String> options;

    /**
     * 构造函数.
     *
     * <p>
     * 由于没有检查参数，不推荐外部使用该构造函数.
     * </p>
     *
     * @param slotName 复制槽名称.
     * @param statusInterval 向primary服务器报告状态信息的时间间隔.
     * @param startPosition 复制槽开始位置.
     * @param options 复制槽选项信息.
     */
    private LogicalStreamStarter //
        /* */( String slotName //
        /* */, int statusInterval //
        /* */, long startPosition //
        /* */, ImmutableMap<String, String> options //
        /* */)
    {
        this.slotName = slotName;
        this.statusInterval = statusInterval;
        this.startPosition = startPosition;
        this.options = options;
    }

    /**
     * 字符串表示信息.
     *
     * <p>
     * 该字符串是人类可读的字符串信息，虽然有一定的格式，但并不适合作为机器解析之用.
     * </p>
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("slotName=");
        sb.append(this.slotName);
        sb.append(", statusInterval=");
        sb.append(this.statusInterval);
        sb.append(", startPosition=");
        sb.append(LogSequenceNumber.valueOf(this.startPosition));
        for (Map.Entry<String, String> e : this.options.entrySet()) {
            sb.append(", options[");
            sb.append(e.getKey());
            sb.append("]=");
            sb.append(e.getValue());
        }
        return sb.toString();
    }

    /**
     * 启动一个PostgreSQL复制流.
     *
     * @param pgc PostgreSQL连接.
     *
     * @return 表示PostgreSQL逻辑复制流的{@code PGReplicationStream}对象.
     *
     * @throws SQLException 当启动复制流出现错误时抛出.
     */
    public PGReplicationStream start(PgConnection pgc) throws SQLException
    {
        ChainedLogicalStreamBuilder streamBuilder //
            = pgc.getReplicationAPI()
            .replicationStream()
            .logical()
            .withSlotName(this.slotName)
            .withStatusInterval(this.statusInterval, TimeUnit.MILLISECONDS);
        Properties properties = new Properties();
        for (Map.Entry<String, String> e : this.options.entrySet()) {
            properties.setProperty(e.getKey(), e.getValue());
        }
        streamBuilder = streamBuilder.withSlotOptions(properties);
        LogSequenceNumber pos = LogSequenceNumber.valueOf(this.startPosition);
        if (!Objects.equals(LogSequenceNumber.INVALID_LSN, pos)) {
            streamBuilder = streamBuilder.withStartPosition(pos);
        }
        logger.info("start logical stream: logicalSlot={}", this);
        return streamBuilder.start();
    }
}

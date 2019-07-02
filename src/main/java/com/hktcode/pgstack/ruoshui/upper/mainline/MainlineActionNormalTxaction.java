/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.SQLException;

class MainlineActionNormalTxaction extends MainlineActionNormal
{
    private static final Logger logger = LoggerFactory.getLogger(MainlineActionNormalTxaction.class);

    static MainlineActionNormalTxaction of
        (MainlineConfigTxaction config, MainlineSender sender, MainlineMetricTxaction metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }

        return new MainlineActionNormalTxaction(config, sender, metric);
    }

    private final MainlineConfigTxaction config;

    private final MainlineMetricTxaction metric;

    private MainlineActionNormalTxaction //
    (MainlineConfigTxaction config, MainlineSender sender, MainlineMetricTxaction metric)
    {
        super(sender);
        this.config = config;
        this.metric = metric;
    }

    @Override
    MainlineAction next(PgConnection pgrepl) //
        throws SQLException, InterruptedException
    {
        Thread thread = Thread.currentThread();
        MainlineRecord r = MainlineRecordMetric.of(thread, metric, this.sender.tqueue);
        long timeout = this.config.logDuration;
        this.sender.send(r, timeout, timeout, metric);
        metric.statusInfor = "start logical replication stream";
        try (PGReplicationStream slt = this.config.logicalRepl.start(pgrepl)) {
            while (!this.sender.isDone()) {
                timeout = this.config.waitTimeout;
                long logDuration = this.config.logDuration;
                LogSequenceNumber txactionlsn = metric.txactionLsn;
                metric.statusInfor = "receive logical replication stream message";
                if (r == null) {
                    slt.setFlushedLSN(txactionlsn);
                    slt.setAppliedLSN(txactionlsn);
                    r = this.poll(slt);
                } else if ((r = this.sender.push(r, timeout, logDuration, metric)) != null) {
                    slt.setFlushedLSN(txactionlsn);
                    slt.setAppliedLSN(txactionlsn);
                    slt.forceUpdateStatus();
                }
            }
        }
        metric.statusInfor = "send txation finish record.";
        return MainlineActionFinish.of(config, sender, metric);
    }

    private MainlineRecord poll(PGReplicationStream s)
        throws SQLException, InterruptedException
    {
        ByteBuffer msg = s.readPending();
        ++this.metric.fetchCounts;
        if (msg == null) {
            long waitTimeout = this.config.waitTimeout;
            long logDuration = this.config.logDuration;
            Thread.sleep(waitTimeout);
            this.metric.fetchMillis += waitTimeout;
            long currMillis = System.currentTimeMillis();
            if (currMillis - this.metric.logDatetime >= logDuration) {
                logger.info("readPending() returns null: waitTimeout={}, logDuration={}", waitTimeout, logDuration);
                this.metric.logDatetime = currMillis;
            }
            return null;
        }
        else {
            ++this.metric.recordCount;
            long key = s.getLastReceiveLSN().asLong();
            LogicalMsg val = LogicalMsg.ofLogicalWal(msg);
            return MainlineRecordNormal.of(key, val);
        }
    }

    @Override
    MainlineMetric getMetric()
    {
        return this.metric;
    }
}

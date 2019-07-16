/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatusInnerEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.time.ZonedDateTime;

class PgsqlTxactionMetricNormalTxaction extends PgsqlTxactionMetricNormal
{
    private static final Logger logger = LoggerFactory.getLogger(PgsqlTxactionMetricNormalTxaction.class);

    static PgsqlTxactionMetricNormalTxaction of
        (MainlineConfig config, PgsqlTxactionMetric metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }

        return new PgsqlTxactionMetricNormalTxaction(config, metric);
    }

    private final MainlineConfig config;

    private LogSequenceNumber txactionLsn = LogSequenceNumber.INVALID_LSN;


    private PgsqlTxactionMetricNormalTxaction //
    (MainlineConfig config, PgsqlTxactionMetric metric)
    {
        super(metric.startMillis);
        this.config = config;
        this.recordCount = metric.recordCount;
        this.fetchCounts = metric.fetchCounts;
        this.fetchMillis = metric.fetchMillis;
        this.offerCounts = metric.offerCounts;
        this.offerMillis = metric.offerMillis;
        this.logDatetime = metric.logDatetime;
        this.statusInfor = metric.statusInfor;
    }

    @Override
    PgsqlTxactionMetric next(PgConnection pgrepl, PgsqlTxaction worker) //
        throws SQLException, InterruptedException
    {
        PgsqlTxactionRecord r = null;
        this.statusInfor = "start logical replication stream";
        try (PGReplicationStream slt = this.config.logicalRepl.start(pgrepl)) {
            while (!(worker.newStatus(worker) instanceof SimpleStatusInnerEnd)) {
                long timeout = this.config.waitTimeout;
                long logDuration = this.config.logDuration;
                LogSequenceNumber txactionlsn = this.txactionLsn;
                this.statusInfor = "receive logical replication stream message";
                if (r == null) {
                    slt.setFlushedLSN(txactionlsn);
                    slt.setAppliedLSN(txactionlsn);
                    r = this.poll(slt);
                } else if ((r = worker.push(r, timeout, logDuration, this)) != null) {
                    slt.setFlushedLSN(txactionlsn);
                    slt.setAppliedLSN(txactionlsn);
                    slt.forceUpdateStatus();
                }
            }
        }
        this.statusInfor = "send txation finish record.";
        ZonedDateTime attime = ZonedDateTime.now();
        return PgsqlTxactionMetricFinish.of(config, this, attime);
    }

    private PgsqlTxactionRecord poll(PGReplicationStream s)
        throws SQLException, InterruptedException
    {
        ByteBuffer msg = s.readPending();
        ++this.fetchCounts;
        if (msg == null) {
            long waitTimeout = this.config.waitTimeout;
            long logDuration = this.config.logDuration;
            Thread.sleep(waitTimeout);
            this.fetchMillis += waitTimeout;
            long currMillis = System.currentTimeMillis();
            if (currMillis - this.logDatetime >= logDuration) {
                logger.info("readPending() returns null: waitTimeout={}, logDuration={}", waitTimeout, logDuration);
                this.logDatetime = currMillis;
            }
            return null;
        }
        else {
            ++this.recordCount;
            long key = s.getLastReceiveLSN().asLong();
            LogicalMsg val = LogicalMsg.ofLogicalWal(msg);
            return PgsqlTxactionRecordNormal.of(key, val);
        }
    }
}

/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.txaction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.PGReplicationStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.time.ZonedDateTime;

public class UpperTxaction implements Runnable
{
    public static UpperTxaction of //
        /* */( UpperTxactionConfig config //
        /* */, PgConnection pgrepl //
        /* */, UpperTxactionSender sender //
        /* */) //
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (pgrepl == null) {
            throw new ArgumentNullException("pgrepl");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        return new UpperTxaction(config, pgrepl, sender);
    }

    private static final Logger logger = LoggerFactory.getLogger(UpperTxaction.class);

    private final UpperTxactionConfig config;

    private final PgConnection pgrepl;

    private final UpperTxactionSender sender;

    private UpperTxaction //
        /* */( UpperTxactionConfig config //
        /* */, PgConnection pgrepl //
        /* */, UpperTxactionSender sender //
        /* */) //
    {
        this.config = config;
        this.pgrepl = pgrepl;
        this.sender = sender;
    }

    public void run()
    {
        ZonedDateTime createTime = ZonedDateTime.now();
        UpperTxactionMetric metric = UpperTxactionMetric.of(createTime);
        Thread thread = Thread.currentThread();
        UpperTxactionRecord r = null;
        try {
            r = UpperTxactionRecordMetric.of(thread, metric, this.sender.tqueue);
            long timeout = this.config.logDuration;
            this.sender.send(r, timeout, timeout, metric);
            metric.statusInfor = "start logical replication stream";
            try (PGReplicationStream slt = this.config.logicalRepl.start(pgrepl)) {
                while (!this.sender.isDone()) {
                    timeout = this.config.waitTimeout;
                    long logDuration = this.config.logDuration;
                    metric.statusInfor = "receive logical replication stream message";
                    if (r == null) {
                        slt.setFlushedLSN(metric.txactionLsn);
                        slt.setAppliedLSN(metric.txactionLsn);
                        r = this.poll(slt, metric);
                    } else if ((r = this.sender.push(r, timeout, logDuration, metric)) != null) {
                        slt.setFlushedLSN(metric.txactionLsn);
                        slt.setAppliedLSN(metric.txactionLsn);
                        slt.forceUpdateStatus();
                    }
                }
            }
            timeout = this.config.logDuration;
            metric.statusInfor = "send txation finish record.";
            r = UpperTxactionRecordFinish.of();
            this.sender.send(r, timeout, timeout, metric);
        }
        catch (InterruptedException ex) {
            logger.error("should not be interrupted by other thread.");
            Thread.currentThread().interrupt();
        }
        catch (Exception ex) {
            logger.info("throws exception: ", ex);
            ZonedDateTime endtime = ZonedDateTime.now();
            metric.statusInfor = "throw exception: " + ex.getMessage();
            long timeout = this.config.logDuration;
            r = UpperTxactionRecordThrows.of(endtime, ex);
            try {
                this.sender.send(r, timeout, timeout, metric);
            }
            catch (InterruptedException e) {
                logger.error("should not be interrupted by other thread.");
                Thread.currentThread().interrupt();
            }
        }
    }

    private UpperTxactionRecord poll(PGReplicationStream s, UpperTxactionMetric m)
        throws SQLException, InterruptedException
    {
        ByteBuffer msg = s.readPending();
        ++m.fetchCounts;
        if (msg == null) {
            long waitTimeout = this.config.waitTimeout;
            long logDuration = this.config.logDuration;
            Thread.sleep(waitTimeout);
            m.fetchMillis += waitTimeout;
            long currMillis = System.currentTimeMillis();
            if (currMillis - m.logDatetime >= logDuration) {
                logger.info("readPending() returns null: waitTimeout={}, logDuration={}", waitTimeout, logDuration);
                m.logDatetime = currMillis;
            }
            return null;
        }
        else {
            ++m.recordCount;
            long key = s.getLastReceiveLSN().asLong();
            LogicalMsg val = LogicalMsg.ofLogicalWal(msg);
            return UpperTxactionRecordNormal.of(key, val);
        }
    }
}

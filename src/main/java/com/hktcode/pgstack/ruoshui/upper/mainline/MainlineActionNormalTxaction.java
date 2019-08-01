/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
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
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

class MainlineActionNormalTxaction extends MainlineActionNormal //
    /* */< MainlineActionNormalTxaction //
    /* */, MainlineConfigNormal //
    /* */, MainlineMetricNormalTxaction //
    /* */> //
{
    private static final Logger logger = LoggerFactory.getLogger(MainlineActionNormalTxaction.class);

    static MainlineActionNormalTxaction of(MainlineActionNormalTypelist action)
    {
        if (action == null) {
            throw new ArgumentNullException("config");
        }
        long finish = System.currentTimeMillis();
        MainlineConfigNormal config = action.config;
        MainlineMetricNormalTxaction metric = MainlineMetricNormalTxaction.of(
            action.metric.startMillis, finish
        );
        AtomicReference<SimpleStatus> status = action.status;
        TransferQueue<MainlineRecord> tqueue = action.tqueue;

        return new MainlineActionNormalTxaction(config, metric, status, tqueue);
    }

    private MainlineActionNormalTxaction //
        /* */(MainlineConfigNormal config //
        /* */, MainlineMetricNormalTxaction metric //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */)
    {
        super(config, metric, status, tqueue);
    }

    MainlineAction next(PgConnection pgrepl) //
        throws SQLException, InterruptedException
    {
        MainlineRecordNormal r = null;
        this.metric.statusInfor = "start logical replication stream";
        try (PGReplicationStream slt = this.config.logicalRepl.start(pgrepl)) {
            while (!(this.newStatus(this) instanceof SimpleStatusInnerEnd)) {
                LogSequenceNumber txactionlsn = this.metric.txactionLsn;
                this.metric.statusInfor = "receive logical replication stream message";
                if (r == null) {
                    slt.setFlushedLSN(txactionlsn);
                    slt.setAppliedLSN(txactionlsn);
                    r = this.poll(slt);
                } else if ((r = this.send(r)) != null) {
                    slt.setFlushedLSN(txactionlsn);
                    slt.setAppliedLSN(txactionlsn);
                    slt.forceUpdateStatus();
                }
            }
        }
        this.metric.statusInfor = "send txation finish record.";
        return MainlineActionFinish.of();
    }

    private MainlineRecordNormal poll(PGReplicationStream s)
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
}

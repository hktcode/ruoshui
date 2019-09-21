/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

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

abstract class PgActionReplTxaction extends PgActionRepl
{
    private static final Logger logger = LoggerFactory.getLogger(PgActionReplTxaction.class);

    final PgReportTypelist typelist;

    LogSequenceNumber txactionLsn = LogSequenceNumber.INVALID_LSN;

    PgActionReplTxaction(PgActionDataTypelist action)
    {
        super(action, System.currentTimeMillis());
        this.typelist = PgReportTypelist.of(action, this.actionStart);
        this.logDatetime = action.logDatetime;
    }

    @Override
    public PgAction next(PgConnection pgrepl) //
        throws SQLException, InterruptedException
    {
        PgRecord r = null;
        this.statusInfor = "start logical replication stream";
        try (PGReplicationStream slt = this.config.logicalRepl.start(pgrepl)) {
            while (!(this.newStatus(this) instanceof SimpleStatusInnerEnd)) {
                LogSequenceNumber txactionlsn = this.txactionLsn;
                this.statusInfor = "receive logical replication stream message";
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
        this.statusInfor = "send txation finish record.";
        return PgActionTerminateEnd.of(this);
    }

    private PgRecordLogicalMsg poll(PGReplicationStream s) //
        throws SQLException, InterruptedException
    {
        ByteBuffer msg = s.readPending();
        ++this.fetchCounts;
        if (msg != null) {
            ++this.recordCount;
            long key = s.getLastReceiveLSN().asLong();
            LogicalMsg val = LogicalMsg.ofLogicalWal(msg);
            return PgRecordLogicalMsg.of(key, val);
        }
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

    @Override
    public PgResultNormal pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        this.txactionLsn = lsn;
        return this.get();
    }

    @Override
    public PgResultNormalSnapshot pst(PgConfigSnapshot config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return PgResultNormalSnapshot.of(this.config, this.toRunMetrics());
    }
}

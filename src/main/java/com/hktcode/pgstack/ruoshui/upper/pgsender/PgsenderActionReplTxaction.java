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

abstract class PgsenderActionReplTxaction extends PgsenderActionRepl
{
    private static final Logger logger = LoggerFactory.getLogger(PgsenderActionReplTxaction.class);

    public final PgsenderReportTypelist typelist;

    public LogSequenceNumber txactionLsn = LogSequenceNumber.INVALID_LSN;

    protected PgsenderActionReplTxaction(PgsenderActionDataTypelist action)
    {
        super(action, System.currentTimeMillis());
        this.typelist = PgsenderReportTypelist.of(action, this.actionStart);
        this.logDatetime = action.logDatetime;
    }

    @Override
    public PgsenderAction
    next(PgConnection pgrepl) throws SQLException, InterruptedException
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
        return PgsenderActionTerminateEnd.of(this.config, this.tqueue, this.status, this.toEndMetrics());
    }

    private PgRecordLogicalMsg poll(PGReplicationStream s)
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
            return PgRecordLogicalMsg.of(key, val);
        }
    }

    public abstract PgsenderMetricRunTxaction complete();

    @Override
    public PgsenderResultRun pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        this.txactionLsn = lsn;
        return this.get();
    }

    @Override
    public PgsenderResultRunSnapshot pst(SnapshotConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return PgsenderResultRunSnapshot.of((MainlineConfig)this.config, this.toRunMetrics());
    }
}

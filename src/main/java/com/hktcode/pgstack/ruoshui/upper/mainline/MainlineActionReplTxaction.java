/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatusInnerEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecordNormal;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmReportFetchThread;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmThreadSnapshotLockingRel;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotConfig;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.nio.ByteBuffer;
import java.sql.SQLException;

abstract class MainlineActionReplTxaction extends MainlineActionRepl
{
    private static final Logger logger = LoggerFactory.getLogger(MainlineActionReplTxaction.class);

    public final MainlineReportTypelist typelist;

    public LogSequenceNumber txactionLsn = LogSequenceNumber.INVALID_LSN;

    protected <T extends MainlineActionDataTypelist>
    MainlineActionReplTxaction(T action)
    {
        super(action, System.currentTimeMillis());
        this.typelist = MainlineReportTypelist.of(action, this.actionStart);
        this.logDatetime = action.logDatetime;
    }

    @Override
    MainlineAction next(PgConnection pgrepl) throws SQLException, InterruptedException, ScriptException
    {
        MainlineRecord r = null;
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
        return MainlineActionTerminateEnd.of(this);
    }

    private MainlineRecordNormal poll(PGReplicationStream s)
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
            return MainlineRecordNormal.of(key, val);
        }
    }

    public abstract MainlineMetricRunTxaction complete();

    @Override
    public MainlineResultRun pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        this.txactionLsn = lsn;
        return this.get();
    }

    @Override
    public MainlineResultRunSnapshot pst(SnapshotConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return MainlineResultRunSnapshot.of(this.config, this.toRunMetrics());
    }
}

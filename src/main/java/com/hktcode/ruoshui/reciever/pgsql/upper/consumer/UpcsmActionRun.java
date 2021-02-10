/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.status.SimpleStatusRun;
import com.hktcode.bgsimple.triple.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

public class UpcsmActionRun extends TripleActionRun<UpcsmConfig, UpcsmMetricRun>
{
    private static final Logger logger = LoggerFactory.getLogger(UpcsmActionRun.class);

    public static UpcsmActionRun of //
        /* */( UpcsmConfig config //
        /* */, BlockingQueue<UpperRecordConsumer> comein //
        /* */, SimpleHolder status //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (comein == null) {
            throw new ArgumentNullException("comein");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new UpcsmActionRun(config, comein, status);
    }

    private final BlockingQueue<UpperRecordConsumer> comein;

    LogSequenceNumber txactionLsn = LogSequenceNumber.INVALID_LSN;

    @Override
    public TripleAction<UpcsmConfig, UpcsmMetricRun> next() //
        throws InterruptedException, SQLException
    {
        try (Connection repl = config.srcProperty.replicaConnection()) {
            PgConnection pgrepl = repl.unwrap(PgConnection.class);
            try (PGReplicationStream slt = this.config.logicalRepl.start(pgrepl)) {
                UpperRecordConsumer r = null;
                while (this.status.run(this, this.number) instanceof SimpleStatusRun) {
                    LogSequenceNumber txactionlsn = this.txactionLsn;
                    this.statusInfor = "receive logical replication stream message";
                    if (r == null) {
                        slt.setFlushedLSN(txactionlsn);
                        slt.setAppliedLSN(txactionlsn);
                        r = this.poll(slt);
                    } else if ((r = this.push(r, this.comein)) != null) {
                        slt.setFlushedLSN(txactionlsn);
                        slt.setAppliedLSN(txactionlsn);
                        slt.forceUpdateStatus();
                    }
                }
            }
        }
        logger.info("pgsender complete");
        this.statusInfor = "send txation finish record.";
        UpcsmMetricRun basicMetric = this.toRunMetrics();
        TripleMetricEnd<UpcsmMetricRun> metric = TripleMetricEnd.of(basicMetric);
        return TripleActionEnd.of(this, this.config, metric, this.number);
    }

    private UpperRecordConsumer poll(PGReplicationStream s) //
            throws SQLException, InterruptedException
    {
        ByteBuffer msg = s.readPending();
        ++this.fetchCounts;
        if (msg != null) {
            ++this.recordCount;
            long key = s.getLastReceiveLSN().asLong();
            LogicalMsg val = LogicalMsg.ofLogicalWal(msg);
            return UpperRecordConsumer.of(key, val);
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

    private UpcsmActionRun //
        /* */( UpcsmConfig config //
        /* */, BlockingQueue<UpperRecordConsumer> comein //
        /* */, SimpleHolder status //
        /* */)
    {
        super(status, config, 0);
        this.comein = comein;
    }

    @Override
    public UpcsmMetricRun toRunMetrics()
    {
        return UpcsmMetricRun.of(this);
    }

    public TripleResult pst(LogSequenceNumber lsn) //
            throws InterruptedException
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        this.txactionLsn = lsn;
        return this.get();
    }
}

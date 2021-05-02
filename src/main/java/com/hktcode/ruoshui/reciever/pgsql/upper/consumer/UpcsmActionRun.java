/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvc;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.simple.SimpleAction;
import com.hktcode.simple.SimpleActionRun;
import com.hktcode.simple.SimpleFinish;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;

public class UpcsmActionRun implements SimpleActionRun<UpcsmConfig, UpcsmMetric, UpperExesvc>
{
    private static final Logger logger = LoggerFactory.getLogger(UpcsmActionRun.class);

    public static UpcsmActionRun of()
    {
        return new UpcsmActionRun();
    }

    @Override
    public SimpleAction next(UpcsmConfig config, UpcsmMetric metric, UpperExesvc exesvc) //
            throws InterruptedException, SQLException
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        final Tqueue<UpperRecordConsumer> comein = exesvc.srcqueue;
        try (Connection repl = config.srcProperty.replicaConnection()) {
            PgConnection pgrepl = repl.unwrap(PgConnection.class);
            try (PGReplicationStream slt = config.logicalRepl.start(pgrepl)) {
                UpperRecordConsumer r = null;
                long prevlsn = 0;
                while (exesvc.run(metric).deletets == Long.MAX_VALUE) {
                    long currlsn = metric.txactionLsn.get();
                    if (prevlsn != currlsn) {
                        LogSequenceNumber lsn = LogSequenceNumber.valueOf(currlsn);
                        slt.setFlushedLSN(lsn);
                        slt.setAppliedLSN(lsn);
                        prevlsn = currlsn;
                    }
                    metric.statusInfor = "receive logical replication stream message";
                    if (r == null) {
                        r = poll(config, metric, slt);
                    } else if ((r = comein.push(r)) != null) {
                        slt.forceUpdateStatus();
                    }
                }
            }
        }
        logger.info("pgsender complete");
        metric.statusInfor = "send txation finish record.";
        metric.endDatetime = System.currentTimeMillis();
        return SimpleFinish.of();
    }

    private UpperRecordConsumer poll(UpcsmConfig config, UpcsmMetric metric, PGReplicationStream s) //
            throws SQLException, InterruptedException
    {
        ByteBuffer msg = s.readPending();
        ++metric.fetchCounts;
        if (msg != null) {
            ++metric.fetchRecord;
            long key = s.getLastReceiveLSN().asLong();
            LogicalMsg val = LogicalMsg.ofLogicalWal(msg);
            return UpperRecordConsumer.of(key, val);
        }
        long waitTimeout = config.waitTimeout;
        long logDuration = config.logDuration;
        Thread.sleep(waitTimeout);
        metric.fetchMillis += waitTimeout;
        long currMillis = System.currentTimeMillis();
        if (currMillis - metric.logDatetime >= logDuration) {
            logger.info("readPending() returns null: waitTimeout={}, logDuration={}", waitTimeout, logDuration);
            metric.logDatetime = currMillis;
        }
        return null;
    }

    private UpcsmActionRun()
    {
    }
}

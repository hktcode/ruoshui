/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWkstep;
import com.hktcode.simple.SimpleWkstepAction;
import com.hktcode.simple.SimpleWkstepTheEnd;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;

public class UpcsmWkstepAction implements SimpleWkstepAction<UpcsmWorkerArgval, UpcsmWorkerGauges>
{
    private static final Logger logger = LoggerFactory.getLogger(UpcsmWkstepAction.class);

    private final Tqueue<UpperRecordConsumer> source;

    public static UpcsmWkstepAction of(Tqueue<UpperRecordConsumer> source)
    {
        if (source == null) {
            throw new ArgumentNullException("config");
        }
        return new UpcsmWkstepAction(source);
    }

    @Override
    public SimpleWkstep next(UpcsmWorkerArgval argval, UpcsmWorkerGauges meters, SimpleAtomic holder) //
            throws InterruptedException, SQLException
    {
        if (argval == null) {
            throw new ArgumentNullException("config");
        }
        if (meters == null) {
            throw new ArgumentNullException("meters");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        UpcsmWkstepArgval config = argval.actionInfos.get(0);
        UpcsmWkstepGauges metric = UpcsmWkstepGauges.of();
        meters.actionInfos.add(metric);
        try (Connection repl = config.srcProperty.replicaConnection()) {
            PgConnection pgrepl = repl.unwrap(PgConnection.class);
            try (PGReplicationStream slt = config.logicalRepl.start(pgrepl)) {
                UpperRecordConsumer r = null;
                while (holder.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                    long currlsn = meters.txactionLsn.get();
                    if (meters.reportedLsn != currlsn) {
                        LogSequenceNumber lsn = LogSequenceNumber.valueOf(currlsn);
                        slt.setFlushedLSN(lsn);
                        slt.setAppliedLSN(lsn);
                        meters.reportedLsn = currlsn;
                    }
                    metric.statusInfor = "receive logical replication stream message";
                    if (r == null) {
                        r = poll(config, metric, slt);
                    } else if ((r = source.push(r)) != null) {
                        slt.forceUpdateStatus();
                    }
                }
            }
        }
        logger.info("pgsender complete");
        metric.statusInfor = "send txation finish record.";
        metric.endDatetime = System.currentTimeMillis();
        return SimpleWkstepTheEnd.of();
    }

    private UpperRecordConsumer poll(UpcsmWkstepArgval config, UpcsmWkstepGauges metric, PGReplicationStream s) //
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

    private UpcsmWkstepAction(Tqueue<UpperRecordConsumer> source)
    {
        this.source = source;
    }
}

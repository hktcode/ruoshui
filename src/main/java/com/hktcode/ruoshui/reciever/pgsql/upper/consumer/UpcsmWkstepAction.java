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
            throw new ArgumentNullException("source");
        }
        return new UpcsmWkstepAction(source);
    }

    @Override
    public SimpleWkstep next(UpcsmWorkerArgval argval, UpcsmWorkerGauges gauges, SimpleAtomic atomic) //
            throws InterruptedException, SQLException
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (gauges == null) {
            throw new ArgumentNullException("gauges");
        }
        if (atomic == null) {
            throw new ArgumentNullException("atomic");
        }
        UpcsmWkstepArgval params = argval.actionInfos.get(0);
        UpcsmWkstepGauges meters = UpcsmWkstepGauges.of();
        gauges.actionInfos.add(meters);
        try (Connection repl = params.srcProperty.replicaConnection()) {
            PgConnection pgrepl = repl.unwrap(PgConnection.class);
            try (PGReplicationStream slt = params.logicalRepl.start(pgrepl)) {
                UpperRecordConsumer r = null;
                while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                    long currlsn = gauges.txactionLsn.get();
                    if (gauges.reportedLsn != currlsn) {
                        LogSequenceNumber lsn = LogSequenceNumber.valueOf(currlsn);
                        slt.setFlushedLSN(lsn);
                        slt.setAppliedLSN(lsn);
                        gauges.reportedLsn = currlsn;
                    }
                    meters.statusInfor = "receive logical replication stream message";
                    if (r == null) {
                        r = poll(params, meters, slt);
                    } else if ((r = source.push(r)) != null) {
                        slt.forceUpdateStatus();
                    }
                }
            }
        }
        logger.info("pgsender complete");
        meters.statusInfor = "send txation finish record.";
        meters.endDatetime = System.currentTimeMillis();
        return SimpleWkstepTheEnd.of();
    }

    private UpperRecordConsumer poll(UpcsmWkstepArgval argval, UpcsmWkstepGauges gauges, PGReplicationStream s) //
            throws SQLException, InterruptedException
    {
        ByteBuffer msg = s.readPending();
        ++gauges.fetchCounts;
        if (msg != null) {
            ++gauges.fetchRecord;
            long key = s.getLastReceiveLSN().asLong();
            LogicalMsg val = LogicalMsg.ofLogicalWal(msg);
            return UpperRecordConsumer.of(key, val);
        }
        long waitTimeout = argval.waitTimeout;
        long logDuration = argval.logDuration;
        Thread.sleep(waitTimeout);
        gauges.fetchMillis += waitTimeout;
        long currMillis = System.currentTimeMillis();
        if (currMillis - gauges.logDatetime >= logDuration) {
            logger.info("readPending() returns null: waitTimeout={}, logDuration={}", waitTimeout, logDuration);
            gauges.logDatetime = currMillis;
        }
        return null;
    }

    private UpcsmWkstepAction(Tqueue<UpperRecordConsumer> source)
    {
        this.source = source;
    }
}

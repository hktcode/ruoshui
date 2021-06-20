/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.queue.Xqueue;
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
import java.util.ArrayList;
import java.util.List;

public class UpcsmWkstepAction implements SimpleWkstepAction<UpcsmWorkerArgval, UpcsmWorkerGauges>
{
    private static final Logger logger = LoggerFactory.getLogger(UpcsmWkstepAction.class);

    public static UpcsmWkstepAction of()
    {
        return new UpcsmWkstepAction();
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
        UpperRecordConsumer r;
        int curCapacity = argval.sender.maxCapacity;
        List<UpperRecordConsumer> rhs, lhs = new ArrayList<>(curCapacity);
        int spins = 0, spinsStatus = Xqueue.Spins.RESET;
        long now, logtime = System.currentTimeMillis(), prelsn = 0;
        UpcsmRecverArgval recver = argval.recver;
        try (Connection repl = recver.srcProperty.replicaConnection()) {
            PgConnection pgrepl = repl.unwrap(PgConnection.class);
            try (PGReplicationStream slt = recver.logicalRepl.start(pgrepl)) {
                while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
                    // 未来计划：此处可以提高性能
                    long n = gauges.xidlsn.get();
                    if (prelsn != n) {
                        LogSequenceNumber lsn = LogSequenceNumber.valueOf(n);
                        slt.setFlushedLSN(lsn);
                        slt.setAppliedLSN(lsn);
                        prelsn = n;
                    }
                    int size = lhs.size();
                    int capacity = argval.sender.maxCapacity;
                    long logDuration = argval.xspins.logDuration;
                    if (    (size > 0)
                         // 未来计划：支持bufferCount和maxDuration
                         && (rhs = gauges.sender.push(lhs)) != lhs
                         && (curCapacity != capacity || (lhs = rhs) == null)
                    ) {
                        lhs = new ArrayList<>(capacity);
                        curCapacity = capacity;
                        spins = 0;
                        logtime = System.currentTimeMillis();
                    } else if (size < capacity && (r = poll(gauges, slt)) != null) {
                        lhs.add(r);
                        spins = 0;
                        logtime = System.currentTimeMillis();
                    } else if (logtime + logDuration >= (now = System.currentTimeMillis())) {
                        logger.info("logDuration={}", logDuration);
                        logtime = now;
                    } else {
                        if (spinsStatus == Xqueue.Spins.SLEEP) {
                            slt.forceUpdateStatus();
                        }
                        spinsStatus = gauges.xspins.spins(spins++);
                    }
                }
            }
        }
        logger.info("pgsender complete");
        gauges.finish = System.currentTimeMillis();
        return SimpleWkstepTheEnd.of();
    }

    private UpperRecordConsumer poll(UpcsmWorkerGauges gauges, PGReplicationStream s) //
            throws SQLException
    {
        ByteBuffer msg = s.readPending();
        ++gauges.recver.trycnt;
        if (msg == null) {
            return null;
        }
        ++gauges.recver.rowcnt;
        long key = s.getLastReceiveLSN().asLong();
        LogicalMsg val = LogicalMsg.ofLogicalWal(msg);
        return UpperRecordConsumer.of(key, val);
    }

    private UpcsmWkstepAction()
    {
    }
}

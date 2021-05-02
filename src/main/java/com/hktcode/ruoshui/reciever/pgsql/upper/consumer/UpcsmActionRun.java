/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperExesvr;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.simple.SimpleAction;
import com.hktcode.simple.SimpleActionEnd;
import com.hktcode.simple.SimpleActionRun;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.postgresql.replication.PGReplicationStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;

public class UpcsmActionRun extends SimpleActionRun<UpcsmConfig, UpcsmMetric, UpperExesvr>
{
    private static final Logger logger = LoggerFactory.getLogger(UpcsmActionRun.class);

    public static UpcsmActionRun of(UpcsmConfig config, UpcsmMetric metric, UpperExesvr holder)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return new UpcsmActionRun(config, metric, holder);
    }

    @Override
    public SimpleAction<UpcsmConfig, UpcsmMetric, UpperExesvr> next() //
            throws InterruptedException, SQLException
    {
        final Tqueue<UpperRecordConsumer> comein = this.entity.srcqueue;
        try (Connection repl = this.config.srcProperty.replicaConnection()) {
            PgConnection pgrepl = repl.unwrap(PgConnection.class);
            try (PGReplicationStream slt = this.config.logicalRepl.start(pgrepl)) {
                UpperRecordConsumer r = null;
                long prevlsn = 0;
                while (this.entity.run(metric).deletets == Long.MAX_VALUE) {
                    long currlsn = this.metric.txactionLsn.get();
                    if (prevlsn != currlsn) {
                        LogSequenceNumber lsn = LogSequenceNumber.valueOf(currlsn);
                        slt.setFlushedLSN(lsn);
                        slt.setAppliedLSN(lsn);
                        prevlsn = currlsn;
                    }
                    metric.statusInfor = "receive logical replication stream message";
                    if (r == null) {
                        r = this.poll(config, metric, slt);
                    } else if ((r = comein.push(r)) != null) {
                        slt.forceUpdateStatus();
                    }
                }
            }
        }
        logger.info("pgsender complete");
        this.metric.statusInfor = "send txation finish record.";
        this.metric.endDatetime = System.currentTimeMillis();
        return SimpleActionEnd.of(this.config, this.metric, this.entity);
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

    private UpcsmActionRun(UpcsmConfig config, UpcsmMetric metric, UpperExesvr holder)
    {
        super(config, metric, holder);
    }
}

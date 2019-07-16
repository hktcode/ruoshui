/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.triple.TripleJunction;
import com.hktcode.bgsimple.triple.TripleJunctionConfig;
import com.hktcode.lang.RunnableWithInterrupted;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegSnapshotMsg;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalTxactContext;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlKey;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlVal;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperJunctionMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperProducerRecord;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpperJunction extends TripleJunction
    /* */< UpperJunction //
    /* */, TripleJunctionConfig //
    /* */, UpperJunctionMetric //
    /* */, UpperConsumerRecord //
    /* */, UpperProducerRecord //
    /* */>
    implements RunnableWithInterrupted
{
    public static UpperJunction of //
        /* */( TripleJunctionConfig config
        /* */, AtomicReference<SimpleStatus> status //
        /* */, BlockingQueue<UpperConsumerRecord> comein //
        /* */, BlockingQueue<UpperProducerRecord> getout //
        /* */)
    {
        if (comein == null) {
            throw new ArgumentNullException("getout");
        }
        if (getout == null) {
            throw new ArgumentNullException("getout");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new UpperJunction(comein, getout, status, config);
    }

    private UpperJunction //
        /* */( BlockingQueue<UpperConsumerRecord> comein //
        /* */, BlockingQueue<UpperProducerRecord> getout //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TripleJunctionConfig config //
        /* */)
    {
        super(config, comein, getout, status);
    }

    @Override
    protected List<UpperProducerRecord> convert //
        /* */( UpperConsumerRecord record //
        /* */, UpperJunction worker //
        /* */)
    {
        if (record == null) {
            throw new ArgumentNullException("record");
        }
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        long lsn = record.lsn;
        LogicalMsg msg = record.msg;

        // 根据对PostgreSQL的测试，作为key的lsn不是严格自增长的.
        // 考虑事务B后于事务A开始，但先于事务A提交。
        // 事务A中写入了a和b两条记录，事务B中写入了c和d两条记录。
        // 事务a、b、c、d写入的顺序为a、c、b、d则：
        // 1、事务B的开始lsn大于事务A的lsn。
        // 2、事务B的XID大于事务A的XID
        // 3、a.lsn > c.lsn > b.lsn > d.lsn。
        // 4、事务B先于事务A提交，事务B先于事务A被接收。
        // 5、事务B的提交lsn小于事务A的提交lsn。
        // 此时LSN不是严格自增长.

        if (msg instanceof LogicalTxactBeginsMsg) {
            // metric.curLsnofcmt = ((LogicalTxactBeginsMsg) msg).lsnofcmt;
            // metric.curSequence = 1;
        }
        else if (msg instanceof LogicalBegSnapshotMsg) {
            // metric.curLsnofcmt = lsn;
            // metric.curSequence = 1;
        }

        // TODO: LogicalTxactContext ctx = metric.txidContext;
        LogicalTxactContext ctx = null;
        ImmutableList<PgsqlVal> vallist = PgsqlVal.of(lsn, msg, ctx);
        List<UpperProducerRecord> result = new ArrayList<>();
        for (PgsqlVal val : vallist) {
            // TODO: PgsqlKey key = PgsqlKey.of(metric.curLsnofcmt, metric.curSequence++);
            PgsqlKey key = null;
            UpperProducerRecord d = UpperProducerRecord.of(key, val);
            result.add(d);
        }
        return ImmutableList.copyOf(result);
    }

    @Override
    public void runWithInterrupted() throws InterruptedException
    {
        ZonedDateTime startMillis = ZonedDateTime.now();
        UpperJunctionMetric metric = UpperJunctionMetric.of(startMillis);
        super.run("upper-junction", this);
    }

    @Override
    public JsonNode toJsonObject()
    {
        // TODO:
        return null;
    }
}

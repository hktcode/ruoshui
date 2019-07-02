/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgtriple.naive.NaiveJunction;
import com.hktcode.bgtriple.naive.NaiveJunctionConfig;
import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegSnapshotMsg;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalTxactContext;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlKey;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlVal;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperJunctionMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpperJunction extends NaiveJunction
    /* */< UpperConsumer //
    /* */, UpperJunction //
    /* */, UpperProducer //
    /* */, NaiveJunctionConfig //
    /* */, UpperJunctionMutableMetric //
    /* */, UpperConsumerRecord //
    /* */, UpperProducerRecord //
    /* */>
{
    public static UpperJunction of //
        /* */( NaiveJunctionConfig config
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status //
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

    private static final Logger logger = LoggerFactory.getLogger(UpperJunction.class);

    private UpperJunction //
        /* */( BlockingQueue<UpperConsumerRecord> comein //
        /* */, BlockingQueue<UpperProducerRecord> getout //
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> bgstatus //
        /* */, NaiveJunctionConfig config //
        /* */)
    {
        super(config, UpperJunctionMutableMetric.of(), comein, getout, bgstatus);
    }

    @Override
    protected List<UpperProducerRecord> convert(UpperConsumerRecord record)
    {
        if (record == null) {
            throw new ArgumentNullException("record");
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
            metric.curLsnofcmt = ((LogicalTxactBeginsMsg) msg).lsnofcmt;
            metric.curSequence = 1;
        }
        else if (msg instanceof LogicalBegSnapshotMsg) {
            metric.curLsnofcmt = lsn;
            metric.curSequence = 1;
        }

        LogicalTxactContext ctx = this.metric.txidContext;
        ImmutableList<PgsqlVal> vallist = PgsqlVal.of(lsn, msg, ctx);
        List<UpperProducerRecord> result = new ArrayList<>();
        for (PgsqlVal val : vallist) {
            PgsqlKey key = PgsqlKey.of(metric.curLsnofcmt, metric.curSequence++);
            UpperProducerRecord d = UpperProducerRecord.of(key, val);
            result.add(d);
        }
        //logger.info("key={}, val={}", key, val);
        return ImmutableList.copyOf(result);
    }
}

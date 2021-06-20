/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.google.common.collect.ImmutableList;
import com.hktcode.ruoshui.reciever.pgsql.upper.*;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWkstep;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlKey;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlVal;
import com.hktcode.simple.SimpleWkstepAction;
import com.hktcode.simple.SimpleWkstepTheEnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UpjctWkstepAction implements SimpleWkstepAction<UpjctWorkerArgval, UpjctWorkerGauges>
{
    private static final Logger logger = LoggerFactory.getLogger(UpjctWkstepAction.class);

    public static UpjctWkstepAction of()
    {
        return new UpjctWkstepAction();
    }

    private UpjctWkstepAction()
    {
    }

    @Override
    public SimpleWkstep next(UpjctWorkerArgval argval, UpjctWorkerGauges gauges, SimpleAtomic atomic) //
            throws InterruptedException
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
        UpjctWkstepArgval a = argval.actionInfos.get(0);
        UpjctWkstepGauges g = UpjctWkstepGauges.of();
        gauges.actionInfos.add(g);
        List<UpperRecordConsumer> crhs = gauges.fetchMetric.list(), clhs;
        List<UpperRecordProducer> plhs = gauges.offerMetric.list(), prhs;
        int curCapacity = gauges.offerMetric.xqueue.maxCapacity;
        int spins = 0;
        long ln, lt = System.currentTimeMillis();
        Iterator<UpperRecordProducer> piter = plhs.iterator();
        Iterator<UpperRecordConsumer> citer = crhs.iterator();
        while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
            int size = plhs.size();
            int capacity = gauges.offerMetric.xqueue.maxCapacity;
            long ld = a.logDuration;
            if (    (size > 0)
                 // 未来计划：支持bufferCount和maxDuration
                 && (prhs = gauges.offerMetric.push(plhs)) != plhs
                 && (curCapacity != capacity || (plhs = prhs) == null)
            ) {
                plhs = new ArrayList<>(capacity);
                curCapacity = capacity;
                spins = 0;
                lt = System.currentTimeMillis();
            } else if (size >= capacity) {
                gauges.spinsMetric.spins(spins++);
            } else if (piter.hasNext()) {
                plhs.add(piter.next());
                spins = 0;
                lt = System.currentTimeMillis();
            } else if (citer.hasNext()) {
                piter = this.convert(g, citer.next()).iterator();
                lt = System.currentTimeMillis();
            } else if ((clhs = gauges.fetchMetric.poll(crhs)) != crhs) {
                crhs = clhs;
                citer = crhs.iterator();
            } else if (lt + ld >= (ln = System.currentTimeMillis())) {
                logger.info("logDuration={}", ld);
                lt = ln;
            } else {
                gauges.spinsMetric.spins(spins++);
            }
        }
        logger.info("upjct complete");
        g.endDatetime = System.currentTimeMillis();
        return SimpleWkstepTheEnd.of();
    }

    private List<UpperRecordProducer> convert(UpjctWkstepGauges gauges, UpperRecordConsumer record)
    {
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
            gauges.curLsnofcmt = ((LogicalTxactBeginsMsg) msg).lsnofcmt;
            gauges.curSequence = 1;
        }

        ImmutableList<PgsqlVal> vallist = PgsqlVal.of(lsn, msg, gauges.txidContext);
        List<UpperRecordProducer> result = new ArrayList<>();
        for (PgsqlVal val : vallist) {
            PgsqlKey key = PgsqlKey.of(gauges.curLsnofcmt, gauges.curSequence++, gauges.txidContext.committs);
            UpperRecordProducer d = UpperRecordProducer.of(key, val);
            result.add(d);
        }
        return ImmutableList.copyOf(result);
    }
}

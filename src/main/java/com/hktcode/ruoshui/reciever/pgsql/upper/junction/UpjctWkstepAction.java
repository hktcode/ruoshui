/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.google.common.collect.ImmutableList;
import com.hktcode.queue.Xqueue;
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

public class UpjctWkstepAction implements SimpleWkstepAction<UpjctWorkerArgval, UpjctWorkerArgval>
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
    public SimpleWkstep next(UpjctWorkerArgval argval, UpjctWorkerArgval gauges, SimpleAtomic atomic) //
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
        List<UpperRecordConsumer> crhs = argval.recver.list(), clhs;
        List<UpperRecordProducer> plhs = argval.sender.list(), prhs;
        int curCapacity = argval.sender.maxCapacity;
        int spins = 0;
        long ln, lt = System.currentTimeMillis();
        Iterator<UpperRecordProducer> piter = plhs.iterator();
        Iterator<UpperRecordConsumer> citer = crhs.iterator();
        Xqueue.Offer<UpperRecordProducer> sender = argval.sender.offerXqueue();
        Xqueue.Fetch<UpperRecordConsumer> recver = argval.recver.fetchXqueue();
        while (atomic.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
            int size = plhs.size(), capacity = argval.sender.maxCapacity;
            long ld = argval.xspins.logDuration;
            if (    (size > 0)
                 // 未来计划：支持bufferCount和maxDuration
                 && (prhs = sender.push(plhs)) != plhs
                 && (curCapacity != capacity || (plhs = prhs) == null)
            ) {
                plhs = new ArrayList<>(capacity);
                curCapacity = capacity;
                spins = 0;
                lt = System.currentTimeMillis();
            } else if (size >= capacity) {
                gauges.xspins.spins(spins++);
            } else if (piter.hasNext()) {
                plhs.add(piter.next());
                spins = 0;
                lt = System.currentTimeMillis();
            } else if (citer.hasNext()) {
                piter = this.convert(gauges, citer.next()).iterator();
                lt = System.currentTimeMillis();
            } else if ((clhs = recver.poll(crhs)) != crhs) {
                crhs = clhs;
                citer = crhs.iterator();
            } else if (lt + ld >= (ln = System.currentTimeMillis())) {
                logger.info("logDuration={}", ld);
                lt = ln;
            } else {
                gauges.xspins.spins(spins++);
            }
        }
        logger.info("upjct complete");
        gauges.finish = System.currentTimeMillis();
        return SimpleWkstepTheEnd.of();
    }

    private List<UpperRecordProducer> convert(UpjctWorkerArgval gauges, UpperRecordConsumer record)
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
            gauges.curlsn = ((LogicalTxactBeginsMsg) msg).lsnofcmt;
            gauges.curseq = 1;
        }

        ImmutableList<PgsqlVal> vallist = PgsqlVal.of(lsn, msg, gauges.xidenv);
        List<UpperRecordProducer> result = new ArrayList<>();
        for (PgsqlVal val : vallist) {
            PgsqlKey key = PgsqlKey.of(gauges.curlsn, gauges.curseq++, gauges.xidenv.committs);
            UpperRecordProducer d = UpperRecordProducer.of(key, val);
            result.add(d);
        }
        return ImmutableList.copyOf(result);
    }
}

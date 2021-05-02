/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.google.common.collect.ImmutableList;
import com.hktcode.ruoshui.reciever.pgsql.upper.*;
import com.hktcode.simple.SimpleAction;
import com.hktcode.queue.Tqueue;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlKey;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlVal;
import com.hktcode.simple.SimpleActionRun;
import com.hktcode.simple.SimpleFinish;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UpjctActionRun implements SimpleActionRun<UpjctConfig, UpjctMetric, UpperExesvc>
{
    public static UpjctActionRun of()
    {
        return new UpjctActionRun();
    }

    private UpjctActionRun()
    {
    }

    @Override
    public SimpleAction next(UpjctConfig config, UpjctMetric metric, UpperExesvc exesvc) //
            throws InterruptedException
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
        UpperRecordConsumer r = null;
        UpperRecordProducer o = null;
        final Tqueue<UpperRecordProducer> getout = exesvc.tgtqueue;
        final Tqueue<UpperRecordConsumer> comein = exesvc.srcqueue;
        Iterator<UpperRecordProducer> t //
            = ImmutableList.<UpperRecordProducer>of().iterator();
        while (exesvc.run(metric).deletets == Long.MAX_VALUE) {
            if (o != null) {
                o = getout.push(o);
            }
            else if (t.hasNext()) {
                o = t.next();
            }
            else if (r == null) {
                r = comein.poll();
            }
            else {
                t = this.convert(metric, r).iterator();
                r = null;
            }
        }
        return SimpleFinish.of();
    }

    private List<UpperRecordProducer> convert(UpjctMetric metric, UpperRecordConsumer record)
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
            metric.curLsnofcmt = ((LogicalTxactBeginsMsg) msg).lsnofcmt;
            metric.curSequence = 1;
        }

        ImmutableList<PgsqlVal> vallist = PgsqlVal.of(lsn, msg, metric.txidContext);
        List<UpperRecordProducer> result = new ArrayList<>();
        for (PgsqlVal val : vallist) {
            PgsqlKey key = PgsqlKey.of(metric.curLsnofcmt, metric.curSequence++, metric.txidContext.committs);
            UpperRecordProducer d = UpperRecordProducer.of(key, val);
            result.add(d);
        }
        return ImmutableList.copyOf(result);
    }
}

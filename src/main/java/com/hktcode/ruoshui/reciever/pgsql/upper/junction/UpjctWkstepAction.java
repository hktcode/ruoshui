/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.google.common.collect.ImmutableList;
import com.hktcode.ruoshui.reciever.pgsql.upper.*;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWkstep;
import com.hktcode.queue.Tqueue;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlKey;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlVal;
import com.hktcode.simple.SimpleWkstepAction;
import com.hktcode.simple.SimpleWkstepTheEnd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UpjctWkstepAction implements SimpleWkstepAction<UpjctWorkerArgval, UpjctWorkerGauges>
{
    public static UpjctWkstepAction of(UpperQueues queues)
    {
        if (queues == null) {
            throw new ArgumentNullException("queues");
        }
        return new UpjctWkstepAction(queues);
    }

    public final UpperQueues queues;

    private UpjctWkstepAction(UpperQueues queues)
    {
        this.queues = queues;
    }

    @Override
    public SimpleWkstep next(UpjctWorkerArgval argval, UpjctWorkerGauges gauges, SimpleAtomic holder) //
            throws InterruptedException
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (gauges == null) {
            throw new ArgumentNullException("gauges");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        UpjctWkstepGauges meters = UpjctWkstepGauges.of();
        UpperRecordConsumer r = null;
        UpperRecordProducer o = null;
        final Tqueue<UpperRecordProducer> getout = this.queues.target;
        final Tqueue<UpperRecordConsumer> comein = this.queues.source;
        Iterator<UpperRecordProducer> t //
            = ImmutableList.<UpperRecordProducer>of().iterator();
        while (holder.call(Long.MAX_VALUE).deletets == Long.MAX_VALUE) {
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
                t = this.convert(meters, r).iterator();
                r = null;
            }
        }
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

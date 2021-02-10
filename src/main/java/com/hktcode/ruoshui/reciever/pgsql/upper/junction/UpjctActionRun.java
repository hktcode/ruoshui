/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.status.SimpleStatusRun;
import com.hktcode.bgsimple.triple.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.ruoshui.reciever.pgsql.entity.LogicalTxactContext;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlKey;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlVal;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

class UpjctActionRun extends TripleActionRun<TripleJunctionConfig, UpjctMetricRun>
{
    public static UpjctActionRun of //
        /* */( TripleJunctionConfig config
        /* */, BlockingQueue<UpperRecordConsumer> comein
        /* */, BlockingQueue<UpperRecordProducer> getout
        /* */, SimpleHolder status
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (comein == null) {
            throw new ArgumentNullException("comein");
        }
        if (getout == null) {
            throw new ArgumentNullException("getout");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new UpjctActionRun(config, comein, getout, status);
    }

    private final BlockingQueue<UpperRecordConsumer> comein;

    private final BlockingQueue<UpperRecordProducer> getout;

    long curLsnofcmt = 0;

    long curSequence = 0;

    private final LogicalTxactContext txidContext;

    private UpjctActionRun //
        /* */( TripleJunctionConfig config
        /* */, BlockingQueue<UpperRecordConsumer> comein
        /* */, BlockingQueue<UpperRecordProducer> getout
        /* */, SimpleHolder status
        /* */)
    {
        super(status, config, 1);
        this.comein = comein;
        this.getout = getout;
        this.txidContext = LogicalTxactContext.of();
    }

    public TripleAction<TripleJunctionConfig, UpjctMetricRun>
    next() throws InterruptedException
    {
        UpperRecordConsumer r = null;
        UpperRecordProducer o = null;
        Iterator<UpperRecordProducer> t //
            = ImmutableList.<UpperRecordProducer>of().iterator();
        while (this.status.run(this, this.number) instanceof SimpleStatusRun) {
            if (o != null) {
                o = this.push(o, getout);
            }
            else if (t.hasNext()) {
                o = t.next();
            }
            else if (r == null) {
                r = this.poll(comein);
            }
            else {
                t = this.convert(r).iterator();
                r = null;
            }
        }
        UpjctMetricRun basicMetric = this.toRunMetrics();
        TripleMetricEnd<UpjctMetricRun> metric = TripleMetricEnd.of(basicMetric);
        return TripleActionEnd.of(this, config, metric, this.number);
    }

    private List<UpperRecordProducer> convert(UpperRecordConsumer record)
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
            this.curLsnofcmt = ((LogicalTxactBeginsMsg) msg).lsnofcmt;
            this.curSequence = 1;
        }

        ImmutableList<PgsqlVal> vallist = PgsqlVal.of(lsn, msg, txidContext);
        List<UpperRecordProducer> result = new ArrayList<>();
        for (PgsqlVal val : vallist) {
            PgsqlKey key = PgsqlKey.of(this.curLsnofcmt, this.curSequence++);
            UpperRecordProducer d = UpperRecordProducer.of(key, val);
            result.add(d);
        }
        return ImmutableList.copyOf(result);
    }

    @Override
    public UpjctMetricRun toRunMetrics()
    {
        return UpjctMetricRun.of(this);
    }
}

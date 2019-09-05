/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.junction;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.bgsimple.triple.TripleJunctionConfig;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalBegSnapshotMsg;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.pgstack.ruoshui.pgsql.LogicalTxactContext;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlKey;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlVal;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.producer.UpperProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

class UpjctActionRun extends SimpleWorker<UpjctAction> implements UpjctAction
{
    private static final Logger logger = LoggerFactory.getLogger(UpjctActionRun.class);

    public static UpjctActionRun of //
        /* */( TripleJunctionConfig config
        /* */, BlockingQueue<UpperConsumerRecord> comein
        /* */, BlockingQueue<UpperProducerRecord> getout
        /* */, AtomicReference<SimpleStatus> status
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

    public final TripleJunctionConfig config;

    private final BlockingQueue<UpperConsumerRecord> comein;

    private final BlockingQueue<UpperProducerRecord> getout;

    public final long actionStart;

    long recordCount = 0;

    long fetchCounts = 0;

    long fetchMillis = 0;

    long offerCounts = 0;

    long offerMillis = 0;

    /**
     * 描述当前状态的信息.
     */
    String statusInfor = "";

    long curLsnofcmt = 0;

    long curSequence = 0;

    private final LogicalTxactContext txidContext;

    private long logDatetime = 0;

    private UpjctActionRun //
        /* */( TripleJunctionConfig config
        /* */, BlockingQueue<UpperConsumerRecord> comein
        /* */, BlockingQueue<UpperProducerRecord> getout
        /* */, AtomicReference<SimpleStatus> status
        /* */)
    {
        super(status, 1);
        this.config = config;
        this.comein = comein;
        this.getout = getout;
        this.actionStart = System.currentTimeMillis();
        this.txidContext = LogicalTxactContext.of();
    }

    protected UpperConsumerRecord poll() throws InterruptedException
    {
        long waitTimeout = config.waitTimeout;
        long logDuration = config.logDuration;
        long startsMillis = System.currentTimeMillis();
        UpperConsumerRecord record //
            = this.comein.poll(waitTimeout, TimeUnit.MILLISECONDS);
        long finishMillis = System.currentTimeMillis();
        this.fetchMillis += (finishMillis - startsMillis);
        ++this.fetchCounts;
        long currMillis = System.currentTimeMillis();
        if (record != null) {
            this.logDatetime = currMillis;
        }
        else if (currMillis - this.logDatetime >= logDuration) {
            logger.info("poll record from getout timeout" //
                    + ": waitTimeout={}" //
                    + ", logDuration={}" //
                    + ", logDatetime={}" //
                    + ", currMillis={}" //
                , waitTimeout, logDuration, this.logDatetime, currMillis);
            this.logDatetime = currMillis;
        }
        return record;
    }

    public UpjctAction next() throws InterruptedException
    {
        UpperConsumerRecord r = null;
        UpperProducerRecord o = null;
        Iterator<UpperProducerRecord> t //
            = ImmutableList.<UpperProducerRecord>of().iterator();
        while (super.newStatus(this) instanceof SimpleStatusInnerRun) {
            if (o != null) {
                o = this.push(o);
            }
            else if (t.hasNext()) {
                o = t.next();
            }
            else if (r == null) {
                r = this.poll();
            }
            else {
                t = this.convert(r).iterator();
                r = null;
            }
        }
        return UpjctActionEnd.of(this);
    }

    private UpperProducerRecord push(UpperProducerRecord record) //
        throws InterruptedException
    {
        long waitTimeout = config.waitTimeout;
        long startsMillis = System.currentTimeMillis();
        boolean success //
            = this.getout.offer(record, waitTimeout, TimeUnit.MILLISECONDS);
        long finishMillis = System.currentTimeMillis();
        this.offerMillis += (finishMillis - startsMillis);
        ++this.offerCounts;
        if (success) {
            ++this.recordCount;
            return null;
        }
        else {
            long logDuration = config.logDuration;
            long currMillis = System.currentTimeMillis();
            if (currMillis - this.logDatetime >= logDuration) {
                logger.info("push record to comein fail" //
                        + ": timeout={}, logDuration={}" //
                    , waitTimeout, logDuration);
                this.logDatetime = currMillis;
            }
            return record;
        }
    }

    private List<UpperProducerRecord> convert(UpperConsumerRecord record)
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
        else if (msg instanceof LogicalBegSnapshotMsg) {
            this.curLsnofcmt = lsn;
            this.curSequence = 1;
        }

        ImmutableList<PgsqlVal> vallist = PgsqlVal.of(lsn, msg, txidContext);
        List<UpperProducerRecord> result = new ArrayList<>();
        for (PgsqlVal val : vallist) {
            PgsqlKey key = PgsqlKey.of(this.curLsnofcmt, this.curSequence++);
            UpperProducerRecord d = UpperProducerRecord.of(key, val);
            result.add(d);
        }
        return ImmutableList.copyOf(result);
    }

    @Override
    public UpjctResultRun get()
    {
        UpjctMetricRun metric = UpjctMetricRun.of(this);
        return UpjctResultRun.of(config, metric);
    }

    @Override
    public UpjctResultEnd del()
    {
        UpjctMetricEnd metric = UpjctMetricEnd.of(this);
        return UpjctResultEnd.of(config, metric);
    }

    @Override
    public UpjctActionErr next(Throwable throwsError)
    {
        return UpjctActionErr.of(this, throwsError);
    }
}

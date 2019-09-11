/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgConfigMainline;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgConfigSnapshot;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class UpcsmActionRun //
    extends SimpleWorker<UpcsmAction> implements UpcsmAction
{
    private static final Logger logger = LoggerFactory.getLogger(UpcsmActionRun.class);

    public static UpcsmActionRun of //
        /* */(PgConfigMainline config //
        /* */, BlockingQueue<UpperRecordConsumer> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (comein == null) {
            throw new ArgumentNullException("comein");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new UpcsmActionRun(config, comein, status);
    }

    public final PgConfigMainline config;

    private final BlockingQueue<UpperRecordConsumer> comein;

    public final long actionStart;

    public long recordCount = 0;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    public long offerCounts = 0;

    public long offerMillis = 0;

    public long logDatetime = 0;

    public String statusInfor = "";

    public UpcsmSender fetchThread;

    public UpcsmAction next() throws InterruptedException, ExecutionException, ScriptException
    {
        UpperRecordConsumer r = null;
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            r = (r == null ? this.poll() : this.push(r));
        }
        return UpcsmActionEnd.of(this, this.fetchThread.get());
    }

    private UpperRecordConsumer poll() throws InterruptedException
    {
        long waitTimeout = this.config.waitTimeout;
        long logDuration = this.config.logDuration;
        long startMillis = System.currentTimeMillis();
        UpperRecordConsumer r = this.fetchThread.poll(waitTimeout, this);
        long finishMillis = System.currentTimeMillis();
        this.fetchMillis += (startMillis - finishMillis);
        ++this.fetchCounts;
        if (r != null) {
            this.logDatetime = finishMillis;
        }
        else if (finishMillis - this.logDatetime >= logDuration) {
            logger.info("poll returns null: waitTimeout={}, logDuration={}" //
                , waitTimeout, logDuration);
            this.logDatetime = finishMillis;
        }
        return r;
    }

    private UpperRecordConsumer push(UpperRecordConsumer record) //
        throws InterruptedException
    {
        long waitTimeout = config.waitTimeout;
        long startsMillis = System.currentTimeMillis();
        boolean success = comein.offer(record, waitTimeout, TimeUnit.MILLISECONDS);
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
                logger.info("push record to comein fail: waitTimeout={}, logDuration={}" //
                    , waitTimeout, logDuration);
                this.logDatetime = currMillis;
            }
            return record;
        }
    }

    @Override
    public UpcsmActionErr next(Throwable throwsError) //
        throws InterruptedException
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return UpcsmActionErr.of(this, this.fetchThread.get(), throwsError);
    }

    private UpcsmActionRun //
        /* */(PgConfigMainline config //
        /* */, BlockingQueue<UpperRecordConsumer> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        super(status, 0);
        this.config = config;
        this.comein = comein;
        this.actionStart = System.currentTimeMillis();
        this.fetchThread = UpcsmSenderMainline.of(config);
    }

    @Override
    public UpcsmResult put() throws InterruptedException
    {
        UpcsmReportSender fetchThreadReport = this.fetchThread.put();
        UpcsmMetricRun metric = UpcsmMetricRun.of(this, fetchThreadReport);
        return UpcsmResultRun.of(metric);
    }

    @Override
    public UpcsmResultRun get() throws InterruptedException
    {
        UpcsmReportSender fetchThreadReport = this.fetchThread.get();
        UpcsmMetricRun metric = UpcsmMetricRun.of(this, fetchThreadReport);
        return UpcsmResultRun.of(metric);
    }

    @Override
    public UpcsmResultEnd del() throws InterruptedException
    {
        UpcsmReportSender fetchThreadReport = this.fetchThread.del();
        UpcsmMetricEnd metric = UpcsmMetricEnd.of(this, fetchThreadReport);
        return UpcsmResultEnd.of(metric);
    }

    @Override
    public UpcsmResult pst(LogSequenceNumber lsn) throws InterruptedException
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        UpcsmReportSender fetchThreadReport = this.fetchThread.pst(lsn);
        UpcsmMetricRun metric = UpcsmMetricRun.of(this, fetchThreadReport);
        return UpcsmResultRun.of(metric);
    }

    @Override
    public UpcsmResult pst(UpperSnapshotPstParams params) //
        throws InterruptedException
    {
        if (params == null) {
            throw new ArgumentNullException("params");
        }
        PgConfigSnapshot config = params.toConfig(this.config);
        UpcsmSender oldThread = this.fetchThread;
        UpcsmSender newThread = this.fetchThread.pst(config);
        if (oldThread == newThread) {
            return this.get();
        }
        this.fetchThread = newThread;
        UpcsmReportSender fetchThreadReport =  this.fetchThread.put();
        UpcsmMetricRun metric = UpcsmMetricRun.of(this, fetchThreadReport);
        return UpcsmResultRun.of(metric);
    }
}

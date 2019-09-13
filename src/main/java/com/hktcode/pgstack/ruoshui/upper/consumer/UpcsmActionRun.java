/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.bgsimple.triple.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgConfigSnapshot;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class UpcsmActionRun extends TripleActionRun<UpcsmAction, UpcsmConfig, UpcsmMetricRun>
    implements UpcsmAction
{
    private static final Logger logger = LoggerFactory.getLogger(UpcsmActionRun.class);

    public static UpcsmActionRun of //
        /* */( UpcsmConfig config //
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

    private final BlockingQueue<UpperRecordConsumer> comein;

    public UpcsmSender fetchThread;

    @Override
    public TripleAction<UpcsmAction, UpcsmConfig, UpcsmMetricRun> //
    next() throws InterruptedException
    {
        UpperRecordConsumer r = null;
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            r = (r == null ? this.poll() : this.push(r));
        }
        UpcsmMetricRun basicMetric = this.toRunMetrics();
        TripleMetricEnd<UpcsmMetricRun> metric = TripleMetricEnd.of(basicMetric);
        return UpcsmActionEnd.of(this, super.config, metric, this.number);
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

    private UpcsmActionRun //
        /* */( UpcsmConfig config //
        /* */, BlockingQueue<UpperRecordConsumer> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        super(status, config, 0);
        this.comein = comein;
        this.fetchThread = UpcsmSenderMainline.of(config.mainlineCfg);
    }

    @Override
    public TripleResultRun<UpcsmAction, UpcsmConfig, UpcsmMetricRun>
    put() throws InterruptedException
    {
        UpcsmReportSender fetchThreadReport = this.fetchThread.put();
        UpcsmMetricRun metric = UpcsmMetricRun.of(this, fetchThreadReport);
        return TripleResultRun.of(super.config, metric);
    }

    public TripleResultRun<UpcsmAction, UpcsmConfig, UpcsmMetricRun>
    get() throws InterruptedException
    {
        UpcsmReportSender fetchThreadReport = this.fetchThread.get();
        UpcsmMetricRun metric = UpcsmMetricRun.of(this, fetchThreadReport);
        return TripleResultRun.of(super.config, metric);
    }

    @Override
    public TripleResultEnd<UpcsmAction, UpcsmConfig, UpcsmMetricRun> //
    del() throws InterruptedException
    {
        UpcsmReportSender fetchThreadReport = this.fetchThread.del();
        UpcsmMetricRun run = UpcsmMetricRun.of(this, fetchThreadReport);
        TripleMetricEnd<UpcsmMetricRun> metric = TripleMetricEnd.of(run);
        return TripleResultEnd.of(super.config, metric);
    }

    @Override
    public UpcsmMetricRun toRunMetrics() throws InterruptedException
    {
        UpcsmReportSender fetchThreadReport = this.fetchThread.get();
        return UpcsmMetricRun.of(this, fetchThreadReport);
    }

    public TripleResultRun<UpcsmAction, UpcsmConfig, UpcsmMetricRun>
    pst(LogSequenceNumber lsn) throws InterruptedException
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        UpcsmReportSender fetchThreadReport = this.fetchThread.pst(lsn);
        UpcsmMetricRun metric = UpcsmMetricRun.of(this, fetchThreadReport);
        return TripleResultRun.of(super.config, metric);
    }

    public TripleResultRun<UpcsmAction, UpcsmConfig, UpcsmMetricRun>
    pst(UpcsmParamsPstSnapshot params) throws InterruptedException
    {
        if (params == null) {
            throw new ArgumentNullException("params");
        }
        PgConfigSnapshot config = params.toConfig(super.config.mainlineCfg);
        UpcsmSender oldThread = this.fetchThread;
        UpcsmSender newThread = this.fetchThread.pst(config);
        if (oldThread == newThread) {
            return this.get();
        }
        this.fetchThread = newThread;
        UpcsmReportSender fetchThreadReport =  this.fetchThread.put();
        UpcsmMetricRun metric = UpcsmMetricRun.of(this, fetchThreadReport);
        return TripleResultRun.of(super.config, metric);
    }

    @Override
    public UpcsmActionErr next(Throwable throwsError) throws InterruptedException
    {
        return UpcsmActionErr.of(this, throwsError);
    }
}

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
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgResultEnd;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
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
    public TripleAction<UpcsmAction, UpcsmConfig, UpcsmMetricRun> next() //
        throws InterruptedException
    {
        UpperRecordConsumer r = null;
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            r = (r == null ? this.poll() : this.push(r, comein));
        }
        return UpcsmActionEnd.of(this);
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
    public TripleResult<UpcsmAction> put() throws InterruptedException
    {
        UpcsmReportSender sender = this.fetchThread.put();
        return this.build(sender);
    }

    @Override
    public TripleResult<UpcsmAction> get() throws InterruptedException
    {
        UpcsmReportSender sender = this.fetchThread.get();
        return this.build(sender);
    }

    @Override
    public TripleResultEnd<UpcsmAction, UpcsmConfig, UpcsmMetricRun> del() //
        throws InterruptedException
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

    @Override
    public TripleResult<UpcsmAction> pst(LogSequenceNumber lsn) //
        throws InterruptedException
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        UpcsmReportSender sender = this.fetchThread.pst(lsn);
        return this.build(sender);
    }

    @Override
    public TripleResult<UpcsmAction> pst(UpcsmParamsPstSnapshot params) //
        throws InterruptedException
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
        UpcsmReportSender sender =  this.fetchThread.put();
        return this.build(sender);
    }

    @Override
    public UpcsmActionErr next(Throwable throwsError) throws InterruptedException
    {
        return UpcsmActionErr.of(this, throwsError);
    }

    private TripleResult<UpcsmAction> build(UpcsmReportSender sender)
    {
        UpcsmMetricRun metric = UpcsmMetricRun.of(this, sender);
        if (sender.mainline instanceof PgResultEnd) {
            TripleMetricEnd<UpcsmMetricRun> m = TripleMetricEnd.of(metric);
            return TripleResultEnd.of(super.config, m);
        }
        else {
            return TripleResultRun.of(super.config, metric);
        }
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class UpcsmActionRun //
    extends SimpleWorker<UpcsmAction> implements UpcsmAction
{
    private static final Logger logger = LoggerFactory.getLogger(UpcsmActionRun.class);

    public static UpcsmActionRun of //
        /* */( MainlineConfig config //
        /* */, BlockingQueue<UpperConsumerRecord> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) throws InterruptedException //
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

    public final MainlineConfig config;

    private final BlockingQueue<UpperConsumerRecord> comein;

    public final long actionStart;

    public long recordCount = 0;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    public long offerCounts = 0;

    public long offerMillis = 0;

    public long logDatetime = 0;

    public String statusInfor = "";

    public UpcsmThread fetchThread;

    public UpcsmAction next() throws InterruptedException, ExecutionException
    {
        UpperConsumerRecord r = null;
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            r = (r == null ? this.poll() : this.push(r));
        }
        return UpcsmActionEnd.of(this);
    }

    private UpperConsumerRecord poll() throws InterruptedException
    {
        long waitTimeout = this.config.waitTimeout;
        long logDuration = this.config.logDuration;
        long startMillis = System.currentTimeMillis();
        UpperConsumerRecord r = this.fetchThread.poll(waitTimeout);
        long finishMillis = System.currentTimeMillis();
        this.fetchMillis += (startMillis - finishMillis);
        ++this.fetchCounts;
        if (r == null && finishMillis - this.logDatetime >= logDuration) {
            logger.info("poll returns null: waitTimeout={}, logDuration={}" //
                , waitTimeout, logDuration);
        }
        this.logDatetime = finishMillis;
        return r;
    }

    private UpperConsumerRecord push(UpperConsumerRecord record) //
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
    public UpcsmActionErr next(Throwable throwable) //
        throws InterruptedException
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        return UpcsmActionErr.of(this, throwable);
    }

    private UpcsmActionRun //
        /* */( MainlineConfig config //
        /* */, BlockingQueue<UpperConsumerRecord> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) throws InterruptedException // TODO:
    {
        super(status, 0);
        this.config = config;
        this.comein = comein;
        this.actionStart = System.currentTimeMillis();
        this.fetchThread = MainlineThread.of(config);
    }

    @Override
    public UpcsmResultRun pst()
    {
        return this.get();
    }

    @Override
    public UpcsmResultRun put()
    {
        return this.get();
    }

    @Override
    public UpcsmResultRun get()
    {
        UpcsmMetricRun metric = UpcsmMetricRun.of(this);
        return UpcsmResultRun.of(metric);
    }

    @Override
    public UpcsmResultEnd del()
    {
        UpcsmMetricEnd metric = UpcsmMetricEnd.of(this);
        return UpcsmResultEnd.of(metric);
    }
}

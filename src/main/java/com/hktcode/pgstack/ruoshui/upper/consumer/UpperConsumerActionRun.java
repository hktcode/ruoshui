/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.method.SimpleMethodDelResult;
import com.hktcode.bgsimple.method.SimpleMethodGetResult;
import com.hktcode.bgsimple.method.SimpleMethodPstResult;
import com.hktcode.bgsimple.method.SimpleMethodPutResult;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class UpperConsumerActionRun //
    extends SimpleWorker<UpperConsumerActionRun> //
    implements UpperConsumerAction<UpperConsumerActionRun>
{
    private static final Logger logger = LoggerFactory.getLogger(UpperConsumerActionRun.class);

    public static UpperConsumerActionRun of //
        /* */( MainlineConfig config //
        /* */, BlockingQueue<UpperConsumerRecord> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) //
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
        return new UpperConsumerActionRun(config, comein, status);
    }

    public final MainlineConfig config;

    public final BlockingQueue<UpperConsumerRecord> comein;

    public final long actionStart;

    public long recordCount = 0;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    public long offerCounts = 0;

    public long offerMillis = 0;

    public long logDatetime = 0;

    public UpperConsumerThread fetchThread = UpperConsumerThreadNoop.of();

    public UpperConsumerAction next() throws InterruptedException
    {
        // this.fetchThread = this.config.createsThread();
        UpperConsumerRecord r = null;
        while (this.newStatus(this) instanceof SimpleStatusInnerRun) {
            r = (r == null ? this.poll() : this.push(r));
        }
        String statusInfor = this.fetchThread.del();
        return UpperConsumerActionEnd.of(this, statusInfor);
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
    public UpperConsumerActionErr next(Throwable throwable) //
        throws InterruptedException
    {
        return null;
    }

    private UpperConsumerActionRun //
        /* */( MainlineConfig config //
        /* */, BlockingQueue<UpperConsumerRecord> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) //
    {
        super(status, 3);
        this.config = config;
        this.comein = comein;
        this.actionStart = System.currentTimeMillis();
    }

    @Override
    public SimpleMethodPstResult<UpperConsumerActionRun> pst()
    {
        return null;
    }

    @Override
    public SimpleMethodPutResult<UpperConsumerActionRun> put()
    {
        return null;
    }

    @Override
    public SimpleMethodGetResult<UpperConsumerActionRun> get()
    {
        return null;
    }

    @Override
    public SimpleMethodDelResult<UpperConsumerActionRun> del()
    {
        return null;
    }
}

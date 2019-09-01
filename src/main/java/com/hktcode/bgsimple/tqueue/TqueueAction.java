/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.tqueue;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.bgsimple.status.SimpleStatusOuter;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TqueueAction //
    <A extends BgWorker<A>, C extends TqueueConfig, R>
{
    private static final Logger logger = LoggerFactory.getLogger(TqueueAction.class);

    public final C config;

    public final TransferQueue<R> tqueue;

    public final AtomicReference<SimpleStatus> status;

    protected TqueueAction //
        /* */( C config //
        /* */, TransferQueue<R> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) //
    {
        this.config = config;
        this.tqueue = tqueue;
        this.status = status;
    }

    public long recordCount = 0;

    public long offerCounts = 0;

    public long offerMillis = 0;

    public long logDatetime = 0;

    public String statusInfor = "";

    public R send(R record) throws InterruptedException
    {
        if (record == null) {
            throw new ArgumentNullException("record");
        }
        long waitTimeout = config.waitTimeout;
        long startsMillis = System.currentTimeMillis();
        boolean success //
            = this.tqueue.tryTransfer(record, waitTimeout, TimeUnit.MILLISECONDS);
        long finishMillis = System.currentTimeMillis();
        this.offerMillis += (finishMillis - startsMillis);
        ++this.offerCounts;
        if (success) {
            ++this.recordCount;
            return null;
        }
        long logDuration = config.logDuration;
        long currMillis = System.currentTimeMillis();
        if (currMillis - this.logDatetime >= logDuration) {
            logger.info("tryTransfer timeout: waitTimeout={}, logDuration={}" //
                , waitTimeout, logDuration);
            this.logDatetime = currMillis;
        }
        return record;
    }

    public SimpleStatusInner newStatus(A wkstep) //
        throws InterruptedException
    {
        SimpleStatus origin;
        while (!((origin = this.status.get()) instanceof SimpleStatusInner)) {
            SimpleStatusOuter outer = (SimpleStatusOuter) origin;
            outer.newStatus(wkstep, 0);
        }
        return (SimpleStatusInner) origin;
    }
}

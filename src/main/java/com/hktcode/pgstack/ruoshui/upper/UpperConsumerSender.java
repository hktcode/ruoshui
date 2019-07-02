/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.bgtriple.status.TripleEndBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgSnapshotSender;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperRunnableMetric;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class UpperConsumerSender<T, M extends UpperRunnableMetric>
    implements PgSnapshotSender<SnapshotMetric>
{
    public final TransferQueue<T> tqueue;

    public final AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status;

    protected UpperConsumerSender
        /* */( TransferQueue<T> tqueue
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status
        /* */)
    {
        this.tqueue = tqueue;
        this.status = status;
    }

    private static final Logger logger = LoggerFactory.getLogger(UpperConsumerSender.class);

    public T push(T record, long timeout, long logDuration, M metric) //
        throws InterruptedException
    {
        if (record == null) {
            throw new ArgumentNullException("record");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        ++metric.offerCounts;
        if (this.tqueue.tryTransfer(record, timeout, TimeUnit.MILLISECONDS)) {
            return null;
        }
        else {
            long currMillis = System.currentTimeMillis();
            metric.offerMillis += timeout;
            if (currMillis - metric.logDatetime >= logDuration) {
                logger.info("tryTransfer() returns false: timeout={}, logDuration={}", timeout, logDuration);
                metric.logDatetime = currMillis;
            }
            return record;
        }
    }

    public void send(T record, long timeout, long logDuration, M metric) //
        throws InterruptedException
    {
        if (record == null) {
            throw new ArgumentNullException("record");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        do {
            record = this.push(record, timeout, logDuration, metric);
            if (this.isDone()) {
                throw new InterruptedException(); // TODO:
            }
        } while (record != null);
    }

    public boolean isDone()
    {
        TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer> s = status.get();
        return s == null || s instanceof TripleEndBgStatus;
    }

    // TODO: remove this
    @Override
    public SnapshotMetric snapshotMetric(ZonedDateTime startMillis)
    {
        if (startMillis == null) {
            throw new ArgumentNullException("startMillis");
        }
        return SnapshotMetric.of(startMillis);
    }

    @Override
    public void sendStatusInfo(String statusInfo, SnapshotMetric metric)
    {
        if (statusInfo == null) {
            throw new ArgumentNullException("statusInfo");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        metric.statusInfor = statusInfo;
    }
}

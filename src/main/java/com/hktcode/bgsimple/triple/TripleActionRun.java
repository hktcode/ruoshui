/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.tqueue.TqueueConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class TripleActionRun<C extends TqueueConfig, M extends TripleMetricRun> //
    extends SimpleWorker implements TripleAction<C, M>
{
    private static final Logger logger = LoggerFactory.getLogger(TripleActionRun.class);

    public final C config;

    public final long actionStart;

    public long recordCount = 0;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    public long offerCounts = 0;

    public long offerMillis = 0;

    public long logDatetime = 0;

    /**
     * 描述当前状态的信息.
     */
    public String statusInfor = "";

    protected TripleActionRun(SimpleHolder status, C config, int number)
    {
        super(status, number);
        this.config = config;
        this.actionStart = System.currentTimeMillis();
    }

    protected <T> T poll(BlockingQueue<T> queue) throws InterruptedException
    {
        long waitTimeout = config.waitTimeout;
        long logDuration = config.logDuration;
        long startsMillis = System.currentTimeMillis();
        T record = queue.poll(waitTimeout, TimeUnit.MILLISECONDS);
        long finishMillis = System.currentTimeMillis();
        this.fetchMillis += (finishMillis - startsMillis);
        ++this.fetchCounts;
        long currMillis = System.currentTimeMillis();
        if (record != null) {
            this.logDatetime = currMillis;
        }
        else if (currMillis - this.logDatetime >= logDuration) {
            logger.info("poll record from queue timeout" //
                    + ": waitTimeout={}" //
                    + ", logDuration={}" //
                    + ", logDatetime={}" //
                    + ", currMillis={}" //
                , waitTimeout, logDuration, this.logDatetime, currMillis);
            this.logDatetime = currMillis;
        }
        return record;
    }

    protected <T> T push(T record, BlockingQueue<T> queue) //
        throws InterruptedException
    {
        long waitTimeout = config.waitTimeout;
        long logDuration = config.logDuration;
        long startsMillis = System.currentTimeMillis();
        boolean success = queue.offer(record, waitTimeout, TimeUnit.MILLISECONDS);
        long finishMillis = System.currentTimeMillis();
        this.offerMillis += (finishMillis - startsMillis);
        ++this.offerCounts;
        if (success) {
            ++this.recordCount;
            return null;
        }
        else if (finishMillis - this.logDatetime >= logDuration) {
            logger.info("push record to queue fail" //
                    + ": waitTimeout={}, logDuration={}" //
                , waitTimeout, logDuration);
            this.logDatetime = finishMillis;
        }
        return record;
    }

    @Override
    public TripleActionErr<C, M> next(Throwable throwsError) throws InterruptedException
    {
        M basicMetric = this.toRunMetrics();
        TripleMetricErr<M> metric = TripleMetricErr.of(basicMetric, throwsError);
        return TripleActionErr.of(this, this.config, metric, this.number);
    }

    @Override
    public TripleResult get() throws InterruptedException
    {
        M metric = this.toRunMetrics();
        return TripleResultRun.of(config, metric);
    }

    @Override
    public TripleResultEnd<C, M> del() throws InterruptedException
    {
        M basicMetric = this.toRunMetrics();
        TripleMetricEnd<M> metric = TripleMetricEnd.of(basicMetric);
        return TripleResultEnd.of(config, metric);
    }

    public abstract M toRunMetrics() throws InterruptedException;
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.status.SimpleStatus;

import java.util.concurrent.atomic.AtomicReference;

public abstract class TripleActionRun<A extends TripleAction<A, C, M>, C, M extends TripleMetricRun>
    extends SimpleWorker<A> implements TripleAction<A, C, M>
{
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

    protected TripleActionRun(AtomicReference<SimpleStatus> status, C config, int number)
    {
        super(status, number);
        this.config = config;
        this.actionStart = System.currentTimeMillis();
    }

    public abstract TripleAction<A, C, M> next() throws InterruptedException;

    @Override
    public TripleActionErr<A, C, M> next(Throwable throwsError) throws InterruptedException
    {
        M basicMetric = this.toRunMetrics();
        TripleMetricErr<M> metric = TripleMetricErr.of(basicMetric, throwsError);
        return TripleActionErr.of(this, this.config, metric, this.number);
    }

    @Override
    public TripleResultRun<A, C, M> get() throws InterruptedException
    {
        M metric = this.toRunMetrics();
        return TripleResultRun.of(config, metric);
    }

    @Override
    public TripleResultEnd<A, C, M> del() throws InterruptedException
    {
        M basicMetric = this.toRunMetrics();
        TripleMetricEnd<M> metric = TripleMetricEnd.of(basicMetric);
        return TripleResultEnd.of(config, metric);
    }

    public abstract M toRunMetrics() throws InterruptedException;
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.tqueue.TqueueConfig;
import com.hktcode.lang.exception.ArgumentNegativeException;
import com.hktcode.lang.exception.ArgumentNullException;

public class TripleActionErr //
    /* */< A extends TripleAction<A, C, M> //
    /* */, C extends TqueueConfig //
    /* */, M extends TripleMetricRun //
    /* */> //
    extends SimpleWorker<A> implements TripleAction<A, C, M>
{
    public static < A extends TripleAction<A, C, M>
             /* */, C extends TqueueConfig
             /* */, M extends TripleMetricRun
             /* */>
    TripleActionErr<A, C, M> of(SimpleWorker<A> action, C config, TripleMetricErr<M> metric, int number) //
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (number < 0) {
            throw new ArgumentNegativeException("number", number);
        }
        return new TripleActionErr<>(action, config, metric, number);
    }

    public static < A extends TripleAction<A, C, M>
             /* */, C extends TqueueConfig
             /* */, M extends TripleMetricRun
             /* */>
    TripleActionErr<A, C, M> of(TripleActionEnd<A, C, M> action, Throwable throwsError) //
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new TripleActionErr<>(action, throwsError);
    }

    public final C config;

    public final TripleMetricErr<M> metric;

    protected TripleActionErr(SimpleWorker<A> action, C config, TripleMetricErr<M> metric, int number) //
    {
        super(action.status, number);
        this.config = config;
        this.metric = metric;
    }

    protected TripleActionErr(TripleActionEnd<A, C, M> action, Throwable throwsError) //
    {
        super(action.status, action.number);
        this.config = action.config;
        this.metric = TripleMetricErr.of(action.metric.basicMetric, throwsError);
    }

    public TripleActionErr<A, C, M> next(Throwable throwsError) //
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return this;
    }

    public TripleResultEnd<A, C, M> del()
    {
        return TripleResultEnd.of(config, metric);
    }
}

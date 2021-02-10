/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.tqueue.TqueueConfig;
import com.hktcode.lang.exception.ArgumentNullException;

public class TripleActionEnd //
    /* */< A extends TripleAction<A, C, M> //
    /* */, C extends TqueueConfig //
    /* */, M extends TripleMetricRun //
    /* */> //
    extends SimpleWorker<A> implements TripleAction<A, C, M>
{
    public static < A extends TripleAction<A, C, M> //
             /* */, C extends TqueueConfig //
             /* */, M extends TripleMetricRun //
             /* */> //
    TripleActionEnd<A, C, M> of(SimpleWorker<A> action, C config, TripleMetricEnd<M> metric, int number)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new TripleActionEnd<>(action, config, metric, number);
    }

    public final C config;

    public final TripleMetricEnd<M> metric;

    protected TripleActionEnd(SimpleWorker<A> action, C config, TripleMetricEnd<M> metric, int number)
    {
        super(action.status, number);
        this.config = config;
        this.metric = metric;
    }

    public TripleResultEnd<A, C, M> del()
    {
        return TripleResultEnd.of(this.config, this.metric);
    }

    public TripleActionErr<A, C, M> next(Throwable throwsError)
    {
        TripleMetricErr<M> m = TripleMetricErr.of(this.metric.basicMetric, throwsError);
        return TripleActionErr.of(this, this.config, m, this.number);
    }
}

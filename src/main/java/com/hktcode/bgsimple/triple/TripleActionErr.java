/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.method.SimpleMethodDel;
import com.hktcode.bgsimple.method.SimpleMethodDelParamsDefault;
import com.hktcode.bgsimple.status.SimpleStatusOuter;
import com.hktcode.bgsimple.tqueue.TqueueConfig;
import com.hktcode.lang.exception.ArgumentNegativeException;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

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

    public final C config;

    public final TripleMetricErr<M> metric;

    protected TripleActionErr(SimpleWorker<A> action, C config, TripleMetricErr<M> metric, int number) //
    {
        super(action.status, number);
        this.config = config;
        this.metric = metric;
    }

    @Override
    public TripleActionEnd<A, C, M> next() throws Exception
    {
        SimpleMethodDel<?>[] method = new SimpleMethodDel[] {
            SimpleMethodDelParamsDefault.of(),
            SimpleMethodDelParamsDefault.of(),
            SimpleMethodDelParamsDefault.of(),
        };
        method[this.number] = this.del();
        Phaser phaser = new Phaser(3);
        SimpleStatusOuter del = SimpleStatusOuter.of(phaser, method);
        this.status.run(del);
        return TripleActionEnd.of(this, this.config, this.metric, this.number);
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

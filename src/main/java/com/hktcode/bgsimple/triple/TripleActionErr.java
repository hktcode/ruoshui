/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.method.SimpleMethod;
import com.hktcode.bgsimple.method.SimpleMethodParamsDelDefault;
import com.hktcode.bgsimple.status.SimpleStatusOuter;
import com.hktcode.bgsimple.tqueue.TqueueConfig;
import com.hktcode.lang.exception.ArgumentNegativeException;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class TripleActionErr<C extends TqueueConfig, M extends TripleMetricRun> //
    extends SimpleWorker implements TripleAction<C, M>
{
    public static <C extends TqueueConfig, M extends TripleMetricRun>
    TripleActionErr<C, M> of(SimpleWorker action, C config, TripleMetricErr<M> metric, int number) //
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

    protected TripleActionErr(SimpleWorker action, C config, TripleMetricErr<M> metric, int number) //
    {
        super(action.status, number);
        this.config = config;
        this.metric = metric;
    }

    @Override
    public TripleActionEnd<C, M> next() throws Exception
    {
        SimpleMethod[] method = new SimpleMethod[] {
            SimpleMethodParamsDelDefault.of(),
            SimpleMethodParamsDelDefault.of(),
            SimpleMethodParamsDelDefault.of(),
        };
        method[this.number] = this.del();
        Phaser phaser = new Phaser(3);
        SimpleStatusOuter del = SimpleStatusOuter.of(phaser, method);
        this.status.run(del);
        return TripleActionEnd.of(this, this.config, this.metric, this.number);
    }

    public TripleActionErr<C, M> next(Throwable throwsError) //
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return this;
    }

    public TripleResultEnd<C, M> del()
    {
        return TripleResultEnd.of(config, metric);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.tqueue.TqueueAction;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PgsenderActionThrowsErrors<C extends PgsenderConfig>
    extends TqueueAction<PgsenderAction<C>, C, PgRecord> //
    implements PgsenderAction<C> //
{
    public static <C extends PgsenderConfig> //
    PgsenderActionThrowsErrors<C> of //
        /* */( PgsenderActionData<C> action //
        /* */, Throwable throwsError //
        /* */)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new PgsenderActionThrowsErrors<>(action, throwsError);
    }

    public static <C extends PgsenderConfig> //
    PgsenderActionThrowsErrors<C> of //
        /* */( PgsenderActionTerminateEnd<C> action //
        /* */, Throwable throwsError //
        /* */)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new PgsenderActionThrowsErrors<>(action, throwsError);
    }

    public static <C extends PgsenderConfig>
    PgsenderActionThrowsErrors<C> of //
        /* */( C config //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, PgsenderMetricErr metric
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgsenderActionThrowsErrors<>(config, tqueue, status, metric);
    }

    private PgsenderActionThrowsErrors //
        /* */( PgsenderActionData<C> action //
        /* */, Throwable throwsError //
        /* */)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics().toErrMetrics(throwsError);
    }

    private PgsenderActionThrowsErrors //
        /* */( PgsenderActionTerminateEnd<C> action //
        /* */, Throwable throwsError //
        /* */)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics().toErrMetrics(throwsError);
    }

    private PgsenderActionThrowsErrors
        /* */( C config //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, PgsenderMetricErr metric
        /* */)
    {
        super(config, tqueue, status);
        this.metric = metric;
    }

    public final PgsenderMetricErr metric;

    @Override
    public PgsenderActionThrowsErrors<C> next(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return this;
    }

    @Override
    public PgsenderMetricEnd toEndMetrics()
    {
        return this.metric;
    }

    @Override
    public PgsenderResultErr<C> get()
    {
        return PgsenderResultErr.of(config, metric);
    }

    @Override
    public PgsenderResultErr<C> del()
    {
        return PgsenderResultErr.of(config, metric);
    }
}

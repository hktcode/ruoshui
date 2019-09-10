/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.tqueue.TqueueAction;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PgsenderActionTerminateEnd //
    extends TqueueAction<PgsenderAction, PgsenderConfig, PgRecord> //
    implements PgsenderAction
{
    public static
    PgsenderActionTerminateEnd of(PgsenderActionData action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionTerminateEnd(action);
    }

    public static
    PgsenderActionTerminateEnd of //
        /* */( PgsenderConfig config //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, PgsenderMetricEnd metric //
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
        return new PgsenderActionTerminateEnd(config, tqueue, status, metric);
    }

    public final PgsenderMetricEnd metric;

    private PgsenderActionTerminateEnd(PgsenderActionData action)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics();
    }

    private PgsenderActionTerminateEnd //
        /* */( PgsenderConfig config //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, PgsenderMetricEnd metric //
        /* */)
    {
        super(config, tqueue, status);
        this.metric = metric;
    }

    @Override
    public PgsenderActionThrowsErrors next(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return PgsenderActionThrowsErrors.of(this, throwsError);
    }

    @Override
    public PgsenderMetricEnd toEndMetrics()
    {
        return this.metric;
    }

    @Override
    public PgsenderResultEnd<PgsenderMetricEnd> get()
    {
        return PgsenderResultEnd.of(this.config, this.metric);
    }

    @Override
    public PgsenderResultEnd<PgsenderMetricEnd> del()
    {
        return PgsenderResultEnd.of(this.config, this.metric);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgActionTerminateEnd extends PgAction
{
    public static PgActionTerminateEnd of(PgAction action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionTerminateEnd(action);
    }

    public final PgsenderMetricEnd metric;

    private PgActionTerminateEnd(PgAction action)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics();
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

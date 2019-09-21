/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

class PgActionTerminateEnd extends PgAction
{
    static PgActionTerminateEnd of(PgAction action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionTerminateEnd(action);
    }

    final PgMetricEnd metric;

    private PgActionTerminateEnd(PgAction action)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toMetrics().toEndMetrics();
    }

    @Override
    public PgActionTerminateEnd next()
    {
        return this;
    }

    @Override
    public PgMetric toMetrics()
    {
        return this.metric;
    }

    @Override
    public PgResultFinish del()
    {
        return PgResultFinish.of(this.config, this.metric);
    }
}

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

    public final PgMetricEnd metric;

    private PgActionTerminateEnd(PgAction action)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics();
    }

    @Override
    public PgMetricEnd toEndMetrics()
    {
        return this.metric;
    }

    @Override
    public PgResultFinish get()
    {
        return PgResultFinish.of(this.config, this.metric);
    }

    @Override
    public PgResultFinish del()
    {
        return this.get();
    }
}

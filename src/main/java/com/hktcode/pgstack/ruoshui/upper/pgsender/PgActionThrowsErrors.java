/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgActionThrowsErrors extends PgAction
{
    public static PgActionThrowsErrors of(PgAction action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new PgActionThrowsErrors(action, throwsError);
    }

    private PgActionThrowsErrors(PgAction action, Throwable throwsError)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics().toErrMetrics(throwsError);
    }

    public final PgsenderMetricErr metric;

    @Override
    public PgsenderMetricEnd toEndMetrics()
    {
        return this.metric;
    }

    @Override
    public PgsenderResultErr get()
    {
        return PgsenderResultErr.of(config, metric);
    }

    @Override
    public PgsenderResultErr del()
    {
        return PgsenderResultErr.of(config, metric);
    }
}

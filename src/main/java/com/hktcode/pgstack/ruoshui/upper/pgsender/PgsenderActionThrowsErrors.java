/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderActionThrowsErrors extends PgsenderAction
{
    public static PgsenderActionThrowsErrors of(PgsenderAction action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new PgsenderActionThrowsErrors(action, throwsError);
    }

    private PgsenderActionThrowsErrors(PgsenderAction action, Throwable throwsError)
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

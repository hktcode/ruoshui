/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderResultRun<R, C extends PgsenderConfig<R, C>> implements PgsenderResult<R, C>
{
    public static <R, C extends PgsenderConfig<R, C>>
    PgsenderResultRun<R, C> of(PgsenderConfig<R, C> config, PgsenderMetricRun metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgsenderResultRun<>(config, metric);
    }

    public final PgsenderConfig config;

    public final PgsenderMetricRun metric;

    protected PgsenderResultRun(PgsenderConfig config, PgsenderMetricRun metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

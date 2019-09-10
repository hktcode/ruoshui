/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderResultRun implements PgsenderResult
{
    static PgsenderResultRun of(PgsenderConfig config, PgsenderMetricRun metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgsenderResultRun(config, metric);
    }

    public final PgsenderConfig config;

    public final PgsenderMetricRun metric;

    PgsenderResultRun(PgsenderConfig config, PgsenderMetricRun metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

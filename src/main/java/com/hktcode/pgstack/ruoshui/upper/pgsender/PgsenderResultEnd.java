/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderResultEnd<R, C extends PgsenderConfig, E extends PgsenderMetricEnd> //
    implements PgsenderResult<R, C>, SimpleMethodAllResultEnd<PgsenderAction<R, C>>
{
    public static <R, C extends PgsenderConfig, E extends PgsenderMetricEnd> //
    PgsenderResultEnd<R, C, E> of(C config, E metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgsenderResultEnd<>(config, metric);
    }

    public final C config;

    public final E metric;

    protected PgsenderResultEnd(C config, E metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

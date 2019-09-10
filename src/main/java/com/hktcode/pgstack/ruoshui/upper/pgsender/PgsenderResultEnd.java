/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderResultEnd<E extends PgsenderMetricEnd> //
    implements PgsenderResult, SimpleMethodAllResultEnd<PgsenderAction>
{
    public static <E extends PgsenderMetricEnd> //
    PgsenderResultEnd<E> of(PgsenderConfig config, E metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgsenderResultEnd<>(config, metric);
    }

    public final PgsenderConfig config;

    public final E metric;

    protected PgsenderResultEnd(PgsenderConfig config, E metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

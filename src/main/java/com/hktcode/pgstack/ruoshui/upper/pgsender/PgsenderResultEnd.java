/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderResultEnd<E extends PgMetricEnd> //
    implements PgsenderResult, SimpleMethodAllResultEnd<PgAction>
{
    public static <E extends PgMetricEnd> //
    PgsenderResultEnd<E> of(PgConfig config, E metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgsenderResultEnd<>(config, metric);
    }

    public final PgConfig config;

    public final E metric;

    protected PgsenderResultEnd(PgConfig config, E metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

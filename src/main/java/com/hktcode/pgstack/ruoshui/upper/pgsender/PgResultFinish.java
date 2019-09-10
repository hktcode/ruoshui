/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgResultFinish implements PgResultEnd
{
    public static PgResultFinish of(PgConfig config, PgMetricEnd metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgResultFinish(config, metric);
    }

    public final PgConfig config;

    public final PgMetricEnd metric;

    private PgResultFinish(PgConfig config, PgMetricEnd metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

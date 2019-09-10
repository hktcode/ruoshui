/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgResultThrows implements PgResultErr
{
    public static PgResultThrows of(PgConfig config, PgMetricErr metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgResultThrows(config, metric);
    }

    public PgConfig config;

    public PgMetricErr metric;

    private PgResultThrows(PgConfig config, PgMetricErr metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderResultErr extends PgsenderResultEnd<PgMetricErr>
{
    public static PgsenderResultErr of(PgConfig config, PgMetricErr metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgsenderResultErr(config, metric);
    }

    private PgsenderResultErr(PgConfig config, PgMetricErr metric)
    {
        super(config, metric);
    }
}

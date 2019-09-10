/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderResultRunSnapshot extends PgsenderResultRun
{
    static PgsenderResultRunSnapshot of(MainlineConfig config, PgMetricRun metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgsenderResultRunSnapshot(config, metric);
    }

    private PgsenderResultRunSnapshot(MainlineConfig config, PgMetricRun metric)
    {
        super(config, metric);
    }
}

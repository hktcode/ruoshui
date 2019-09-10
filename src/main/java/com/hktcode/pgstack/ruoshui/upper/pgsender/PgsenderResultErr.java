/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderResultErr extends PgsenderResultEnd<PgsenderMetricErr>
{
    public static PgsenderResultErr of(PgsenderConfig config, PgsenderMetricErr metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgsenderResultErr(config, metric);
    }

    private PgsenderResultErr(PgsenderConfig config, PgsenderMetricErr metric)
    {
        super(config, metric);
    }
}

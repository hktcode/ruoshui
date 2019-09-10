/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderResultErr<C extends PgsenderConfig> //
    extends PgsenderResultEnd<C, PgsenderMetricErr>
{
    public static <C extends PgsenderConfig> //
    PgsenderResultErr<C> of(C config, PgsenderMetricErr metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgsenderResultErr<>(config, metric);
    }

    private PgsenderResultErr(C config, PgsenderMetricErr metric)
    {
        super(config, metric);
    }
}

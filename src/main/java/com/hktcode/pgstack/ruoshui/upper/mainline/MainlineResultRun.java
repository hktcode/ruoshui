/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.method.SimpleMethodAllResultRun;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineResultRun<W extends MainlineAction<W>> //
    implements SimpleMethodAllResultRun<W>
{
    public static <W extends MainlineAction<W>> //
    MainlineResultRun<W> of(MainlineConfig config, MainlineMetric metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metrci");
        }
        return new MainlineResultRun<W>(config, metric);
    }

    public final MainlineConfig config;

    public final MainlineMetric metric;

    private MainlineResultRun(MainlineConfig config, MainlineMetric metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

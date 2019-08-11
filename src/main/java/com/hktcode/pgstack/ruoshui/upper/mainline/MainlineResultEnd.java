/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.method.SimpleMethodAllResultRun;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineResultEnd<W extends MainlineAction<W>> //
    implements SimpleMethodAllResultEnd<W>
{
    public static <W extends MainlineAction<W>> //
    MainlineResultEnd<W> of(MainlineConfig config, MainlineMetricEnd metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new MainlineResultEnd<>(config, metric);
    }

    public final MainlineConfig config;

    public final MainlineMetricEnd metric;

    private MainlineResultEnd(MainlineConfig config, MainlineMetricEnd metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

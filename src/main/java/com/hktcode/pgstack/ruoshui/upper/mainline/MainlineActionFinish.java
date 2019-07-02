/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

class MainlineActionFinish extends MainlineAction
{
    static MainlineActionFinish //
    of(MainlineConfig config, MainlineSender sender, MainlineMetric metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new MainlineActionFinish(config, sender, metric);
    }

    private MainlineActionFinish(MainlineConfig config, MainlineSender sender, MainlineMetric metric)
    {
        super(sender);
        this.config = config;
        this.metric = metric;
    }

    final MainlineConfig config;

    final MainlineMetric metric;

    @Override
    MainlineMetric getMetric()
    {
        return this.metric;
    }
}

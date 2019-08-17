/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;

public class UpperConsumerResultRun //
    implements SimpleMethodAllResultEnd<UpperConsumerActionRun>
{
    public static UpperConsumerResultRun of(MainlineConfig config, UpperConsumerMetricRun metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpperConsumerResultRun(config, metric);
    }

    public final MainlineConfig config;

    public final UpperConsumerMetricRun metric;

    private UpperConsumerResultRun(MainlineConfig config, UpperConsumerMetricRun metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;

public class UpperConsumerResultEnd //
    implements SimpleMethodAllResultEnd<UpperConsumerActionEnd>
{
    public static UpperConsumerResultEnd of(MainlineConfig config, UpperConsumerMetricEnd metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpperConsumerResultEnd(config, metric);
    }

    public final MainlineConfig config;

    public final UpperConsumerMetricEnd metric;

    private UpperConsumerResultEnd(MainlineConfig config, UpperConsumerMetricEnd metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

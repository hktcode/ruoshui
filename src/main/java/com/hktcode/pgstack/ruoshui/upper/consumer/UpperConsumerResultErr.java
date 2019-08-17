/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;

public class UpperConsumerResultErr //
    implements SimpleMethodAllResultEnd<UpperConsumerActionErr>
{
    public static UpperConsumerResultErr of(MainlineConfig config, UpperConsumerMetricErr metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpperConsumerResultErr(config, metric);
    }

    public final MainlineConfig config;

    public final UpperConsumerMetricErr metric;

    private UpperConsumerResultErr(MainlineConfig config, UpperConsumerMetricErr metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

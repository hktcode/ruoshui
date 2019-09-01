/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;

public class UpperConsumerResultRun //
    implements SimpleMethodAllResultEnd<UpperConsumerAction>
{
    public static UpperConsumerResultRun of(UpperConsumerMetricRun metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpperConsumerResultRun(metric);
    }

    public final UpperConsumerMetricRun metric;

    private UpperConsumerResultRun(UpperConsumerMetricRun metric)
    {
        this.metric = metric;
    }
}

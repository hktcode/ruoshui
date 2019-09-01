/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;

public class UpperConsumerResultErr //
    implements SimpleMethodAllResultEnd<UpperConsumerAction>
{
    public static UpperConsumerResultErr of(UpperConsumerMetricErr metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpperConsumerResultErr(metric);
    }

    public final UpperConsumerMetricErr metric;

    private UpperConsumerResultErr(UpperConsumerMetricErr metric)
    {
        this.metric = metric;
    }
}

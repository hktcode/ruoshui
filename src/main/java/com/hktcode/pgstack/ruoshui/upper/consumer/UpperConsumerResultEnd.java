/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;

public class UpperConsumerResultEnd //
    implements SimpleMethodAllResultEnd<UpperConsumerAction>
{
    public static UpperConsumerResultEnd of(UpperConsumerMetricEnd metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UpperConsumerResultEnd(metric);
    }

    public final UpperConsumerMetricEnd metric;

    private UpperConsumerResultEnd(UpperConsumerMetricEnd metric)
    {
        this.metric = metric;
    }
}

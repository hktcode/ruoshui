/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.triple.TripleProducerConfig;
import com.hktcode.lang.exception.ArgumentNullException;

public class UppdcResultErr //
    implements UppdcResult, SimpleMethodAllResultEnd<UppdcAction>
{
    public static UppdcResultErr //
    of(TripleProducerConfig config, UppdcMetricErr metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UppdcResultErr(config, metric);
    }

    public final TripleProducerConfig config;

    public final UppdcMetricErr metric;

    private UppdcResultErr(TripleProducerConfig config, UppdcMetricErr metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

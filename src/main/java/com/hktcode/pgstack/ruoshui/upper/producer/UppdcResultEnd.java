/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.triple.TripleProducerConfig;
import com.hktcode.lang.exception.ArgumentNullException;

public class UppdcResultEnd //
    implements UppdcResult, SimpleMethodAllResultEnd<UppdcAction>
{
    public static UppdcResultEnd //
    of(TripleProducerConfig config, UppdcMetricEnd metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UppdcResultEnd(config, metric);
    }

    public final TripleProducerConfig config;

    public final UppdcMetricEnd metric;

    private UppdcResultEnd(TripleProducerConfig config, UppdcMetricEnd metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

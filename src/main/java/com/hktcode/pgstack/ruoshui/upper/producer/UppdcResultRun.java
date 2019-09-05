/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.bgsimple.triple.TripleProducerConfig;
import com.hktcode.lang.exception.ArgumentNullException;

public class UppdcResultRun implements UppdcResult
{
    public static UppdcResultRun of(TripleProducerConfig config, UppdcMetricRun metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UppdcResultRun(config, metric);
    }

    public final TripleProducerConfig config;

    public final UppdcMetricRun metric;

    private UppdcResultRun(TripleProducerConfig config, UppdcMetricRun metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

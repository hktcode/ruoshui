package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleEntity<C extends SimpleConfig, M extends SimpleMetric>
{
    public static <C extends SimpleConfig, M extends SimpleMetric> //
    SimpleEntity<C, M> of(C config, M metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new SimpleEntity<>(config, metric);
    }

    public final C config;

    public final M metric;

    protected SimpleEntity(C config, M metric)
    {
        this.config = config;
        this.metric = metric;
    }
}

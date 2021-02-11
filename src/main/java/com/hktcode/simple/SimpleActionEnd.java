package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleActionEnd<C extends SimpleConfig, M extends SimpleMetric, H extends SimpleStatus<?>> //
        extends SimpleAction<C, M, H>
{
    public static <C extends SimpleConfig, M extends SimpleMetric, H extends SimpleStatus<?>> //
    SimpleActionEnd<C, M, H> of(C config, M metric, H holder)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return new SimpleActionEnd<>(config, metric, holder);
    }

    protected SimpleActionEnd(C config, M metric, H holder)
    {
        super(config, metric, holder);
    }
}

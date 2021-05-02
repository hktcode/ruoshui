package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleActionEnd<C extends SimpleConfig, M extends SimpleMetric, E extends SimpleExesvc>
        extends SimpleAction<C, M, E>
{
    public static <C extends SimpleConfig, M extends SimpleMetric, E extends SimpleExesvc> //
    SimpleActionEnd<C, M, E> of(C config, M metric, E exesvc)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (exesvc == null) {
            throw new ArgumentNullException("exesvc");
        }
        return new SimpleActionEnd<>(config, metric, exesvc);
    }

    protected SimpleActionEnd(C config, M metric, E exesvc)
    {
        super(config, metric, exesvc);
    }
}

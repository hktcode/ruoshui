package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleActionEnd<C extends SimpleConfig, M extends SimpleMetric, E extends SimpleEntity<?>> //
        extends SimpleAction<C, M, E>
{
    public static <C extends SimpleConfig, M extends SimpleMetric, E extends SimpleEntity<?>> //
    SimpleActionEnd<C, M, E> of(C config, M metric, E entity)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (entity == null) {
            throw new ArgumentNullException("entity");
        }
        return new SimpleActionEnd<>(config, metric, entity);
    }

    protected SimpleActionEnd(C config, M metric, E entity)
    {
        super(config, metric, entity);
    }
}

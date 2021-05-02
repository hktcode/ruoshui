package com.hktcode.simple;

public class SimpleAction<C extends SimpleConfig, M extends SimpleMetric, E extends SimpleEntity>
{
    public final C config;

    public final M metric;

    public final E entity;

    protected SimpleAction(C config, M metric, E entity)
    {
        this.config = config;
        this.metric = metric;
        this.entity = entity;
    }
}

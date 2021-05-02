package com.hktcode.simple;

public class SimpleAction<C extends SimpleConfig, M extends SimpleMetric, E extends SimpleExesvc>
{
    public final C config;

    public final M metric;

    public final E exesvc;

    protected SimpleAction(C config, M metric, E exesvc)
    {
        this.config = config;
        this.metric = metric;
        this.exesvc = exesvc;
    }
}

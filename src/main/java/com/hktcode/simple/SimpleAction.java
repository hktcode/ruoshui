package com.hktcode.simple;

public class SimpleAction<C extends SimpleConfig, M extends SimpleMetric, H extends SimpleEntity<?>>
{
    protected final C config;

    protected final M metric;

    protected final H holder;

    protected SimpleAction(C config, M metric, H holder)
    {
        this.config = config;
        this.metric = metric;
        this.holder = holder;
    }
}

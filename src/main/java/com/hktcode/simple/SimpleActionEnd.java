package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleActionEnd extends SimpleAction
{
    public final SimpleConfig config;

    public final SimpleMetric metric;

    public final SimpleEntity entity;

    protected SimpleActionEnd(SimpleConfig config, SimpleMetric metric, SimpleEntity entity)
    {
        this.config = config;
        this.metric = metric;
        this.entity = entity;
    }

    public static SimpleActionEnd of(SimpleConfig config, SimpleMetric metric, SimpleEntity entity)
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
        return new SimpleActionEnd(config, metric, entity);
    }
}

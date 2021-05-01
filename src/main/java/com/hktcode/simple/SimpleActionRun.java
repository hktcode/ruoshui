/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

public abstract class SimpleActionRun<C extends SimpleConfig, M extends SimpleMetric, E extends SimpleEntity<?>> extends SimpleAction
{
    public final C config;

    public final M metric;

    public final E entity;

    protected SimpleActionRun(C config, M metric, E entity)
    {
        this.config = config;
        this.metric = metric;
        this.entity = entity;
    }

    public abstract SimpleAction next() throws Throwable;

    public SimpleAction next(Throwable throwError) throws InterruptedException
    {
        if (throwError == null) {
            throw new ArgumentNullException("throwError");
        }
        this.metric.throwErrors.add(throwError);
        this.metric.endDatetime = System.currentTimeMillis();
        SimplePhaserOuter del = SimplePhaserOuter.of(3);
        while (this.entity.run(metric).deletets == Long.MAX_VALUE) {
            this.entity.end(del);
        }
        return SimpleActionEnd.of(this.config, this.metric, this.entity);
    }
}

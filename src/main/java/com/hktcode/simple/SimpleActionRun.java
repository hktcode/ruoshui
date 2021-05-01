/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

public abstract class SimpleActionRun<C extends SimpleConfig, M extends SimpleMetric, E extends SimpleEntity<?>>
        extends SimpleAction<C, M, E>
{
    protected SimpleActionRun(C config, M metric, E entity)
    {
        super(config, metric, entity);
    }

    public abstract SimpleAction<C, M, E> next() throws Throwable;

    public SimpleActionEnd<C, M, E> next(Throwable throwError) throws InterruptedException
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

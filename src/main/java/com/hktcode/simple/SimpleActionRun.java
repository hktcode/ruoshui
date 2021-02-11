/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;

public abstract class SimpleActionRun<C extends SimpleConfig, M extends SimpleMetric, H extends SimpleStatus<?>> //
        extends SimpleAction<C, M, H>
{
    protected SimpleActionRun(C config, M metric, H holder)
    {
        super(config, metric, holder);
    }

    public abstract SimpleAction<C, M, H> next() throws Exception;

    public SimpleAction<C, M, H> next(Throwable throwError) throws InterruptedException
    {
        if (throwError == null) {
            throw new ArgumentNullException("throwError");
        }
        this.metric.throwErrors.add(throwError);
        this.metric.endDatetime = System.currentTimeMillis();
        SimplePhaserOuter del = SimplePhaserOuter.of(3);
        while (this.holder.run(metric).deletets == Long.MAX_VALUE) {
            this.holder.end(del);
        }
        return SimpleActionEnd.of(this.config, this.metric, this.holder);
    }
}

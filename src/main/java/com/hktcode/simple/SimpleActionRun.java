/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleActionRun<C extends SimpleConfig, M extends SimpleMetric, E extends SimpleExesvc>
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
        while (this.exesvc.run(metric).deletets == Long.MAX_VALUE) {
            SimpleResult result = this.exesvc.end(del);
            logger.info("end: result={}", result);
        }
        return SimpleActionEnd.of(this.config, this.metric, this.exesvc);
    }

    private static final Logger logger = LoggerFactory.getLogger(SimpleActionRun.class);
}

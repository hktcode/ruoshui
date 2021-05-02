/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SimpleActionRun<C extends SimpleConfig, M extends SimpleMetric, E extends SimpleExesvc>
        extends SimpleAction
{
    SimpleAction next(C config, M metric, E exesvc) throws Throwable;

    default SimpleAction next(C config, M metric, E exesvc, Throwable errors) //
            throws InterruptedException
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
        if (errors == null) {
            throw new ArgumentNullException("errors");
        }
        metric.throwErrors.add(errors);
        metric.endDatetime = System.currentTimeMillis();
        SimplePhaserOuter del = SimplePhaserOuter.of(3);
        while (exesvc.run(metric).deletets == Long.MAX_VALUE) {
            SimpleResult result = exesvc.end(del);
            logger.info("end: result={}", result);
        }
        return SimpleFinish.of();
    }

    Logger logger = LoggerFactory.getLogger(SimpleActionRun.class);
}

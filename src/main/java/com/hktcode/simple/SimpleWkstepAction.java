/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

public interface SimpleWkstepAction<M extends SimpleWorkerMeters, E extends SimpleExesvc>
        extends SimpleWkstep
{
    SimpleWkstep next(M meters, E exesvc) throws Throwable;
}

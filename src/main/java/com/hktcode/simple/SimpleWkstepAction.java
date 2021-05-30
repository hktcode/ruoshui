/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

public interface SimpleWkstepAction<A extends SimpleWorkerArgval, M extends SimpleWorkerMeters>
        extends SimpleWkstep
{
    SimpleWkstep next(A argval, M meters, SimpleHolder holder) throws Throwable;
}

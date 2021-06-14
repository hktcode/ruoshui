/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

public interface SimpleWkstepAction<A extends SimpleWorkerArgval<A, G>, G extends SimpleWorkerGauges>
        extends SimpleWkstep
{
    SimpleWkstep next(A argval, G gauges, SimpleAtomic holder) throws Throwable;
}

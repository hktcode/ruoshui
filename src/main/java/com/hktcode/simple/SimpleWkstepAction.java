/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

public interface SimpleWkstepAction<A extends SimpleWorkerArgval<A>>
        extends SimpleWkstep
{
    SimpleWkstep next(A argval, SimpleAtomic atomic) throws Throwable;
}

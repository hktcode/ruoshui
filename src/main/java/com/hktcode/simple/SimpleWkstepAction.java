/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

public interface SimpleWkstepAction extends SimpleWkstep
{
    SimpleWkstep next(SimpleAtomic atomic) throws Throwable;
}

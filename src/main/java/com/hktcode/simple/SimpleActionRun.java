/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

public interface SimpleActionRun<M extends SimpleMeters, E extends SimpleExesvc>
        extends SimpleAction
{
    SimpleAction next(M meters, E exesvc) throws Throwable;
}

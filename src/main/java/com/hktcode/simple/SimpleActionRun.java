/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.simple;

public abstract class SimpleActionRun<E> extends SimpleAction<E>
{
    protected SimpleActionRun(SimpleHolder<E> holder)
    {
        super(holder);
    }

    public abstract SimpleAction<E> next() throws Exception;

    public abstract SimpleAction<E> next(Throwable throwError, SimpleMetric metric) throws InterruptedException;
}

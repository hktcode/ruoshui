/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.SimpleMethodResult;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleHolder
{
    public static SimpleHolder of(SimpleStatus put)
    {
        if (put == null) {
            throw new ArgumentNullException("put");
        }
        return new SimpleHolder(put);
    }

    private final AtomicReference<SimpleStatus> status;

    private SimpleHolder(SimpleStatus put)
    {
        this.status = new AtomicReference<>(put);
    }

    public SimpleStatusInner newStatus(BgWorker wkstep, int number) throws InterruptedException
    {
        SimpleStatus origin;
        while (!((origin = this.status.get()) instanceof SimpleStatusInner)) {
            SimpleStatusOuter outer = (SimpleStatusOuter) origin;
            outer.newStatus(wkstep, number);
        }
        return (SimpleStatusInner) origin;
    }

    public ImmutableList<SimpleMethodResult> run(SimpleStatusOuter outer)
            throws InterruptedException
    {
        if (outer == null) {
            throw new ArgumentNullException("outer");
        }
        // TODO: 判断get中的bgMethod不是bgResult.
        SimpleStatus origin;
        SimpleStatus future;
        do {
            origin = this.status.get();
            future = origin.outer(outer);
        } while (/*  */future == origin  //
                /**/|| (/**/future == outer  //
                /*     */&& !this.status.compareAndSet(origin, future) //
                /*   */)
            /**/);
        return this.run();
    }

    public ImmutableList<SimpleMethodResult> run() throws InterruptedException
    {
        SimpleStatus origin;
        SimpleStatusInner future;
        do {
            origin = this.status.get();
            future = origin.inner();
        } while (future != origin && !this.status.compareAndSet(origin, future));
        return ImmutableList.copyOf(future.result);
    }
}

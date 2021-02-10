/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple;

import com.hktcode.bgsimple.future.*;
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

    public SimpleStatus get()
    {
        return this.status.get();
    }

    public boolean cas(SimpleStatus expect, SimpleStatus update)
    {
        if (expect == null) {
            throw new ArgumentNullException("expect");
        }
        if (update == null) {
            throw new ArgumentNullException("update");
        }
        return this.status.compareAndSet(expect, update);
    }

    public SimpleFutureOuter put()
    {
        SimpleStatus s = this.status.get();
        // if (!(s instanceof SimpleStatusOuterPut)) {
        //     throw new SimpleStatusIsNotPutException();
        // }
        // SimpleStatusOuterPut put = (SimpleStatusOuterPut)s;
        // return put.newFuture(this);
        return ((SimpleStatusOuter)s).newFuture(this);
    }

    public SimpleFutureOuter outer(SimpleStatusOuter outer)
    {
        if (outer == null) {
            throw new ArgumentNullException("outer");
        }
        // TODO: 判断get中的bgMethod不是bgResult.
        SimpleStatus origin;
        SimpleStatusOuter future;
        do {
            origin = this.status.get();
            future = origin.outer(outer);
        } while (/**/future == origin  //
                || (    future == outer  //
                /*   */&& !this.status.compareAndSet(origin, future) //
                /* */)
            /**/);
        return future.newFuture(this);
    }
}

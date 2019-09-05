/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple;

import com.hktcode.bgsimple.future.SimpleFutureDel;
import com.hktcode.bgsimple.future.SimpleFutureGet;
import com.hktcode.bgsimple.future.SimpleFuturePst;
import com.hktcode.bgsimple.future.SimpleFuturePut;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleHolder
{
    public static SimpleHolder of(AtomicReference<SimpleStatus> status) //
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new SimpleHolder(status);
    }

    private final AtomicReference<SimpleStatus> status;

    private SimpleHolder(AtomicReference<SimpleStatus> status)
    {
        this.status = status;
    }

    public SimpleFuturePut put()
    {
        SimpleStatus s = this.status.get();
        if (!(s instanceof SimpleStatusOuterPut)) {
            // TODO: 抛出异常可能会好点.
            // TODO: 如何确保start只会被调用一次呢？
            // return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            throw new RuntimeException(); // TODO:
        }
        SimpleStatusOuterPut put = (SimpleStatusOuterPut)s;

        return SimpleFuturePut.of(status, put);
    }

    public SimpleFuturePst pst(SimpleStatusOuterPst pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 判断pst中的bgMethod不是bgResult.
        SimpleStatus origin;
        SimpleStatus future;
        do {
            origin = this.status.get();
            future = origin.pst(pst);
        } while (/*  */!(future instanceof SimpleStatusOuterPst) //
                /**/|| future == origin  //
                /**/|| (    future == pst  //
                /*     */&& !this.status.compareAndSet(origin, future) //
                /*   */)
            /**/);
        return SimpleFuturePst.of(status, (SimpleStatusOuterPst)future);
    }

    public SimpleFutureGet get(SimpleStatusOuterGet get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 判断get中的bgMethod不是bgResult.
        SimpleStatus origin;
        SimpleStatus future;
        do {
            origin = this.status.get();
            future = origin.get(get);
        } while (/*  */!(future instanceof SimpleStatusOuterGet) //
                /**/|| future == origin  //
                /**/|| (    future == get  //
                /*     */&& !this.status.compareAndSet(origin, future) //
                /*   */)
            /**/);
        return SimpleFutureGet.of(status, (SimpleStatusOuterGet)future);
    }

    public SimpleFutureDel del(SimpleStatusOuterDel del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 判断del中的bgMethod不是bgResult.
        SimpleStatus origin;
        SimpleStatus future;
        do {
            origin = this.status.get();
            future = origin.del(del);
        } while (/*  */!(future instanceof SimpleStatusOuterDel)  //
                /**/|| future == origin  //
                /**/|| (    future == del //
                /*     */|| !this.status.compareAndSet(origin, future) //
                /*   */) //
            /**/);
        return SimpleFutureDel.of(status, (SimpleStatusOuterDel)future);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterDel;
import com.hktcode.bgsimple.status.SimpleStatusOuterGet;
import com.hktcode.bgsimple.status.SimpleStatusOuterPst;
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

    public SimpleStatusOuterPst pst(SimpleStatusOuterPst pst)
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
                /**/|| (    future != origin  //
                /*     */&& future == pst  //
                /*     */&& !this.status.compareAndSet(origin, future) //
                /*   */)
            /**/);
        return (SimpleStatusOuterPst) future;
    }

    public SimpleStatusOuterGet get(SimpleStatusOuterGet get)
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
                /**/|| (    future != origin  //
                /*     */&& future == get  //
                /*     */&& !this.status.compareAndSet(origin, future) //
                /*   */)
            /**/);
        return (SimpleStatusOuterGet)future;
    }

    public SimpleStatusOuterDel del(SimpleStatusOuterDel del)
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
                /**/|| (    future != origin  //
                /*     */&& future == del //
                /*     */&& !this.status.compareAndSet(origin, future) //
                /*   */) //
            /**/);
        return (SimpleStatusOuterDel)future;
    }
}

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

public class SimpleHolder<W extends SimpleWorker<W, M>, M>
{
    public static <W extends SimpleWorker<W, M>, M> //
    SimpleHolder<W, M> of(AtomicReference<SimpleStatus<W, M>> status) //
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new SimpleHolder<>(status);
    }

    private final AtomicReference<SimpleStatus<W, M>> status;

    private SimpleHolder(AtomicReference<SimpleStatus<W, M>> status)
    {
        this.status = status;
    }

    public SimpleStatusOuterPst<W, M> pst(SimpleStatusOuterPst<W, M> pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 判断pst中的bgMethod不是bgResult.
        SimpleStatus<W, M> origin;
        SimpleStatus<W, M> future;
        do {
            origin = this.status.get();
            future = origin.pst(pst);
        } while (/*  */!(future instanceof SimpleStatusOuterPst) //
                /**/|| (    future != origin  //
                /*     */&& future == pst  //
                /*     */&& !this.status.compareAndSet(origin, future) //
                /*   */)
            /**/);
        return (SimpleStatusOuterPst<W, M>) future;
    }

    public SimpleStatusOuterGet<W, M> get(SimpleStatusOuterGet<W, M> get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 判断get中的bgMethod不是bgResult.
        SimpleStatus<W, M> origin;
        SimpleStatus<W, M> future;
        do {
            origin = this.status.get();
            future = origin.get(get);
        } while (/*  */!(future instanceof SimpleStatusOuterGet) //
                /**/|| (    future != origin  //
                /*     */&& future == get  //
                /*     */&& !this.status.compareAndSet(origin, future) //
                /*   */)
            /**/);
        return (SimpleStatusOuterGet<W, M>)future;
    }

    public SimpleStatusOuterDel<W, M> del(SimpleStatusOuterDel<W, M> del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 判断del中的bgMethod不是bgResult.
        SimpleStatus<W, M> origin;
        SimpleStatus<W, M> future;
        do {
            origin = this.status.get();
            future = origin.del(del);
        } while (/*  */!(future instanceof SimpleStatusOuterDel)  //
                /**/|| (    future != origin  //
                /*     */&& future == del //
                /*     */&& !this.status.compareAndSet(origin, future) //
                /*   */) //
            /**/);
        return (SimpleStatusOuterDel<W, M>)future;
    }
}

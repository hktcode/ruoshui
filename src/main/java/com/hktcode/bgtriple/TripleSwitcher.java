/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgtriple;

import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.bgtriple.status.TripleDelBgStatus;
import com.hktcode.bgtriple.status.TripleGetBgStatus;
import com.hktcode.bgtriple.status.TriplePstBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class TripleSwitcher //
    /* */< S extends TripleConsumer<S, P, D> //
    /* */, P extends TripleJunction<S, P, D> //
    /* */, D extends TripleProducer<S, P, D> //
    /* */> //
{
    public static //
    /* */< S extends TripleConsumer<S, P, D> //
    /* */, P extends TripleJunction<S, P, D> //
    /* */, D extends TripleProducer<S, P, D> //
    /* */> //
    TripleSwitcher<S, P, D> //
    of(AtomicReference<TripleBasicBgStatus<S, P, D>> status)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new TripleSwitcher<>(status);
    }

    protected final AtomicReference<TripleBasicBgStatus<S, P, D>> status;

    protected TripleSwitcher(AtomicReference<TripleBasicBgStatus<S, P, D>> status)
    {
        this.status = status;
    }

    public TriplePstBgStatus<S, P, D> pst(TriplePstBgStatus<S, P, D> pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 判断pst中的bgMethod不是bgResult.
        TripleBasicBgStatus<S, P, D> origin;
        TripleBasicBgStatus<S, P, D> future;
        do {
            origin = this.status.get();
            future = origin.pst(pst);
        } while (/*  */!(future instanceof TriplePstBgStatus) //
            /**/|| (    future != origin  //
            /*     */&& future == pst  //
            /*     */&& !this.status.compareAndSet(origin, future) //
            /*   */)
            /**/);
        return (TriplePstBgStatus<S, P, D>) future;
    }

    public TripleGetBgStatus<S, P, D> get(TripleGetBgStatus<S, P, D> get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 判断get中的bgMethod不是bgResult.
        TripleBasicBgStatus<S, P, D> origin;
        TripleBasicBgStatus<S, P, D> future;
        do {
            origin = this.status.get();
            future = origin.get(get);
        } while (/*  */!(future instanceof TripleGetBgStatus) //
            /**/|| (    future != origin  //
            /*     */&& future == get  //
            /*     */&& !this.status.compareAndSet(origin, future) //
            /*   */)
            /**/);
        return (TripleGetBgStatus<S, P, D>)future;
    }

    public TripleDelBgStatus<S, P, D> del(TripleDelBgStatus<S, P, D> del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 判断del中的bgMethod不是bgResult.
        TripleBasicBgStatus<S, P, D> origin;
        TripleBasicBgStatus<S, P, D> future;
        do {
            origin = this.status.get();
            future = origin.del(del);
        } while (/*  */!(future instanceof TripleDelBgStatus)  //
            /**/|| (    future != origin  //
            /*     */&& future == del //
            /*     */&& !this.status.compareAndSet(origin, future) //
            /*   */) //
            /**/);
        return (TripleDelBgStatus<S, P, D>)future;
    }
}

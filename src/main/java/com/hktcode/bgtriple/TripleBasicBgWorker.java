/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple;

import com.hktcode.bgmethod.BgMethod;
import com.hktcode.bgmethod.BgMethodResult;
import com.hktcode.bgmethod.SimpleBasicEndBgResult;
import com.hktcode.bgmethod.BgMethodParamsDelDefault;
import com.hktcode.bgtriple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TripleBasicBgWorker //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    implements TripleBgWorker<C, J, P>
{
    private static final Logger logger //
        = LoggerFactory.getLogger(TripleBasicBgWorker.class);

    public static //
        /* */< S extends TripleConsumer<S, P, D> //
        /* */, P extends TripleJunction<S, P, D> //
        /* */, D extends TripleProducer<S, P, D> //
        /* */> //
    TripleInnerBgStatus<S, P, D> //
    newStatus( AtomicReference<TripleBasicBgStatus<S, P, D>> status //
        /* */, TripleBgWorker<S, P, D> worker //
        /* */) //
        throws InterruptedException
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        TripleBasicBgStatus<S, P, D> origin;
        while (!((origin = status.get()) instanceof TripleInnerBgStatus)) {
            TripleBasicBgStatus<S, P, D> future = origin.newStatus(worker);
            status.compareAndSet(origin, future);
        }
        return (TripleInnerBgStatus<S, P, D>) origin;
    }

    public static //
        /* */< S extends TripleConsumer<S, P, D> //
        /* */, P extends TripleJunction<S, P, D> //
        /* */, D extends TripleProducer<S, P, D> //
        /* */> //
    TripleInnerBgStatus<S, P, D> //
    newStatus( AtomicReference<TripleBasicBgStatus<S, P, D>> status //
        /* */, TripleBgWorker<S, P, D> worker //
        /* */, Throwable reasons //
        /* */, ZonedDateTime endtime //
        /* */) //
        throws InterruptedException
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        TripleBasicBgStatus<S, P, D> origin;
        while (!((origin = status.get()) instanceof TripleInnerBgStatus)) {
            TripleBasicBgStatus<S, P, D> future = origin.newStatus(worker, reasons, endtime);
            status.compareAndSet(origin, future);
        }
        return (TripleInnerBgStatus<S, P, D>) origin;
    }

    public static //
        /* */< C extends TripleConsumer<C, J, P> //
        /* */, J extends TripleJunction<C, J, P> //
        /* */, P extends TripleProducer<C, J, P> //
        /* */, SM extends BgMethod<C> //
        /* */, PM extends BgMethod<J> //
        /* */, DM extends BgMethod<P> //
        /* */> //
    TripleBasicBgStatus<C, J, P> //
    newStatus( AtomicReference<SM> consumer //
        /* */, AtomicReference<PM> junction //
        /* */, AtomicReference<DM> producer //
        /* */) throws InterruptedException
    {
        BgMethod<C> c;
        while (!((c = consumer.get()) instanceof BgMethodResult)) {
            Thread.sleep(10);
        }
        BgMethod<J> j;
        while (!((j = junction.get()) instanceof BgMethodResult)) {
            Thread.sleep(10);
        }
        BgMethod<P> p;
        while (!((p = producer.get()) instanceof BgMethodResult)) {
            Thread.sleep(10);
        }
        if (c instanceof SimpleBasicEndBgResult) {
            SimpleBasicEndBgResult<C> cr = (SimpleBasicEndBgResult<C>)c;
            if (j instanceof SimpleBasicEndBgResult) {
                SimpleBasicEndBgResult<J> jr = (SimpleBasicEndBgResult<J>)j;
                if (p instanceof SimpleBasicEndBgResult) {
                    SimpleBasicEndBgResult<P> pr = (SimpleBasicEndBgResult<P>)p;
                    return TripleEndBgStatus.of(cr, jr, pr);
                }
                else {
                    return TripleDelBgStatus.of(cr, jr, BgMethodParamsDelDefault.of());
                }
            }
            else if (p instanceof SimpleBasicEndBgResult) {
                SimpleBasicEndBgResult<P> pr = (SimpleBasicEndBgResult<P>) p;
                return TripleDelBgStatus.of(cr, BgMethodParamsDelDefault.of(), pr);
            }
            else {
                return TripleDelBgStatus.of(cr, BgMethodParamsDelDefault.of(), BgMethodParamsDelDefault.of());
            }
        }
        else if (j instanceof SimpleBasicEndBgResult) {
            SimpleBasicEndBgResult<J> jr = (SimpleBasicEndBgResult<J>)j;
            if (p instanceof SimpleBasicEndBgResult) {
                SimpleBasicEndBgResult<P> pr = (SimpleBasicEndBgResult<P>)p;
                return TripleDelBgStatus.of(BgMethodParamsDelDefault.of(), jr, pr);
            }
            else {
                return TripleDelBgStatus.of(BgMethodParamsDelDefault.of(), jr, BgMethodParamsDelDefault.of());
            }
        }
        else if (p instanceof SimpleBasicEndBgResult) {
            SimpleBasicEndBgResult<P> pr = (SimpleBasicEndBgResult<P>) p;
            return TripleDelBgStatus.of(BgMethodParamsDelDefault.of(), BgMethodParamsDelDefault.of(), pr);
        } else {
            return TripleRunBgStatus.of();
        }
    }

    protected final AtomicReference<TripleBasicBgStatus<C, J, P>> status;

    protected //
    TripleBasicBgWorker(AtomicReference<TripleBasicBgStatus<C, J, P>> status)
    {
        this.status = status;
    }

    public TripleSwitcher<C, J, P> switcher()
    {
        return TripleSwitcher.of(this.status);
    }

    @Override
    public TripleInnerBgStatus<C, J, P> newStatus() throws InterruptedException
    {
        return TripleBasicBgWorker.newStatus(this.status, this);
    }

    @Override
    public TripleInnerBgStatus<C, J, P> newStatus(Throwable reasons, ZonedDateTime endtime) throws InterruptedException
    {
        return TripleBasicBgWorker.newStatus(this.status, this, reasons, endtime);
    }
}

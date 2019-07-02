/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple;

import com.hktcode.bgsimple.SimpleBasicBgMethod;
import com.hktcode.bgsimple.SimpleBasicBgResult;
import com.hktcode.bgsimple.SimpleBasicEndBgResult;
import com.hktcode.bgsimple.SimpleDelDefaultBgParams;
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
        /* */, SM extends SimpleBasicBgMethod<C> //
        /* */, PM extends SimpleBasicBgMethod<J> //
        /* */, DM extends SimpleBasicBgMethod<P> //
        /* */> //
    TripleBasicBgStatus<C, J, P> //
    newStatus( AtomicReference<SM> consumer //
        /* */, AtomicReference<PM> junction //
        /* */, AtomicReference<DM> producer //
        /* */) throws InterruptedException
    {
        SimpleBasicBgMethod<C> c;
        while (!((c = consumer.get()) instanceof SimpleBasicBgResult)) {
            Thread.sleep(10);
        }
        SimpleBasicBgMethod<J> j;
        while (!((j = junction.get()) instanceof SimpleBasicBgResult)) {
            Thread.sleep(10);
        }
        SimpleBasicBgMethod<P> p;
        while (!((p = producer.get()) instanceof SimpleBasicBgResult)) {
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
                    return TripleDelBgStatus.of(cr, jr, SimpleDelDefaultBgParams.of());
                }
            }
            else if (p instanceof SimpleBasicEndBgResult) {
                SimpleBasicEndBgResult<P> pr = (SimpleBasicEndBgResult<P>) p;
                return TripleDelBgStatus.of(cr, SimpleDelDefaultBgParams.of(), pr);
            }
            else {
                return TripleDelBgStatus.of(cr, SimpleDelDefaultBgParams.of(), SimpleDelDefaultBgParams.of());
            }
        }
        else if (j instanceof SimpleBasicEndBgResult) {
            SimpleBasicEndBgResult<J> jr = (SimpleBasicEndBgResult<J>)j;
            if (p instanceof SimpleBasicEndBgResult) {
                SimpleBasicEndBgResult<P> pr = (SimpleBasicEndBgResult<P>)p;
                return TripleDelBgStatus.of(SimpleDelDefaultBgParams.of(), jr, pr);
            }
            else {
                return TripleDelBgStatus.of(SimpleDelDefaultBgParams.of(), jr, SimpleDelDefaultBgParams.of());
            }
        }
        else if (p instanceof SimpleBasicEndBgResult) {
            SimpleBasicEndBgResult<P> pr = (SimpleBasicEndBgResult<P>) p;
            return TripleDelBgStatus.of(SimpleDelDefaultBgParams.of(), SimpleDelDefaultBgParams.of(), pr);
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

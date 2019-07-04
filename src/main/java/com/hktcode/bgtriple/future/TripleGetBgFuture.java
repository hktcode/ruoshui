/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.future;

import com.hktcode.bgmethod.BgMethodResult;
import com.hktcode.bgmethod.BgMethodGet;
import com.hktcode.bgmethod.BgMethodGetResult;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.bgtriple.result.TripleGetBgResult;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class TripleGetBgFuture //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    implements TripleBasicBgFuture<TripleGetBgResult<C, J, P>, C, J, P>
{
    public static < C extends TripleConsumer<C, J, P>
        /*      */, J extends TripleJunction<C, J, P>
        /*      */, P extends TripleProducer<C, J, P>
        /*      */>
    TripleGetBgFuture<C, J, P> of //
        /* */(AtomicReference<BgMethodGet<C>> consumer //
        /* */, AtomicReference<BgMethodGet<J>> junction //
        /* */, AtomicReference<BgMethodGet<P>> producer //
        /* */)
    {
        if (consumer == null) {
            throw new ArgumentNullException("consumer");
        }
        if (junction == null) {
            throw new ArgumentNullException("junction");
        }
        if (producer == null) {
            throw new ArgumentNullException("producer");
        }
        return new TripleGetBgFuture<>(consumer, junction, producer);
    }

    private final AtomicReference<BgMethodGet<C>> consumer;

    private final AtomicReference<BgMethodGet<J>> junction;

    private final AtomicReference<BgMethodGet<P>> producer;

    private TripleGetBgFuture //
        /* */(AtomicReference<BgMethodGet<C>> consumer //
        /* */, AtomicReference<BgMethodGet<J>> junction //
        /* */, AtomicReference<BgMethodGet<P>> producer //
        /* */)
    {
        this.consumer = consumer;
        this.junction = junction;
        this.producer = producer;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return false;
    }

    @Override
    public boolean isCancelled()
    {
        return false;
    }

    @Override
    public boolean isDone()
    {
        return consumer.get() instanceof BgMethodResult
            && junction.get() instanceof BgMethodResult
            && producer.get() instanceof BgMethodResult;
    }

    @Override
    public TripleGetBgResult<C, J, P> get() throws InterruptedException, ExecutionException
    {
        BgMethodGet<C> c;
        while (!((c = this.consumer.get()) instanceof BgMethodGetResult)) {
            Thread.sleep(10);
        }
        BgMethodGet<J> j;
        while (!((j = this.junction.get()) instanceof BgMethodGetResult)) {
            Thread.sleep(10);
        }
        BgMethodGet<P> p;
        while (!((p = this.producer.get()) instanceof BgMethodGetResult)) {
            Thread.sleep(10);
        }
        return TripleGetBgResult.of //
            /*     */( (BgMethodGetResult<C>) c //
                /* */, (BgMethodGetResult<J>) j //
                /* */, (BgMethodGetResult<P>) p //
                /* */);
    }

    @Override
    public TripleGetBgResult<C, J, P> get(long timeout, TimeUnit unit) //
        throws InterruptedException, ExecutionException, TimeoutException
    {
        if (unit == null) {
            throw new ArgumentNullException("unit");
        }
        long duration = unit.toMillis(timeout);
        long start = System.currentTimeMillis();
        BgMethodGet<C> c;
        while (!((c = this.consumer.get()) instanceof BgMethodGetResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        BgMethodGet<J> j;
        while (!((j = this.junction.get()) instanceof BgMethodGetResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        BgMethodGet<P> p;
        while (!((p = this.producer.get()) instanceof BgMethodGetResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        return TripleGetBgResult.of //
            /*     */( (BgMethodGetResult<C>) c //
                /* */, (BgMethodGetResult<J>) j //
                /* */, (BgMethodGetResult<P>) p //
                /* */);
    }
}

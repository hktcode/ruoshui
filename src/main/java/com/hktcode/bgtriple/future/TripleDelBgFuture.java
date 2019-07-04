/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.future;

import com.hktcode.bgmethod.BgMethodResult;
import com.hktcode.bgmethod.BgMethodDel;
import com.hktcode.bgmethod.BgMethodDelResult;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.bgtriple.result.TripleDelBgResult;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class TripleDelBgFuture //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    implements TripleBasicBgFuture<TripleDelBgResult<C, J, P>, C, J, P>
{
    public static < S extends TripleConsumer<S, P, D> //
        /*      */, P extends TripleJunction<S, P, D> //
        /*      */, D extends TripleProducer<S, P, D> //
        /*      */>
    TripleDelBgFuture<S, P, D> of //
        /* */( AtomicReference<BgMethodDel<S>> consumer
        /* */, AtomicReference<BgMethodDel<P>> junction
        /* */, AtomicReference<BgMethodDel<D>> producer
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
        return new TripleDelBgFuture<>(consumer, junction, producer);
    }

    private final AtomicReference<BgMethodDel<C>> consumer;

    private final AtomicReference<BgMethodDel<J>> junction;

    private final AtomicReference<BgMethodDel<P>> producer;

    private TripleDelBgFuture
        /* */( AtomicReference<BgMethodDel<C>> consumer
        /* */, AtomicReference<BgMethodDel<J>> junction
        /* */, AtomicReference<BgMethodDel<P>> producer
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
        return consumer.get() instanceof BgMethodResult  //
            && junction.get() instanceof BgMethodResult //
            && producer.get() instanceof BgMethodResult;
    }

    @Override
    public TripleDelBgResult<C, J, P> get() throws InterruptedException, ExecutionException
    {
        BgMethodDel<C> c;
        while (!((c = this.consumer.get()) instanceof BgMethodDelResult)) {
            Thread.sleep(10);
        }
        BgMethodDel<J> j;
        while (!((j = this.junction.get()) instanceof BgMethodDelResult)) {
            Thread.sleep(10);
        }
        BgMethodDel<P> p;
        while (!((p = this.producer.get()) instanceof BgMethodDelResult)) {
            Thread.sleep(10);
        }
        return TripleDelBgResult.of
            /*     */( (BgMethodDelResult<C>) c //
                /* */, (BgMethodDelResult<J>) j //
                /* */, (BgMethodDelResult<P>) p //
                /* */);
    }

    @Override
    public TripleDelBgResult<C, J, P> get(long timeout, TimeUnit unit) //
        throws InterruptedException, ExecutionException, TimeoutException
    {
        if (unit == null) {
            throw new ArgumentNullException("unit");
        }
        long duration = unit.toMillis(timeout);
        long start = System.currentTimeMillis();
        BgMethodDel<C> c;
        while (!((c = this.consumer.get()) instanceof BgMethodDelResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        BgMethodDel<J> j;
        while (!((j = this.junction.get()) instanceof BgMethodDelResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        BgMethodDel<P> p;
        while (!((p = this.producer.get()) instanceof BgMethodDelResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        return TripleDelBgResult.of //
            /*     */( (BgMethodDelResult<C>) c //
                /* */, (BgMethodDelResult<J>) j //
                /* */, (BgMethodDelResult<P>) p //
                /* */);
    }
}

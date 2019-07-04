/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.future;

import com.hktcode.bgmethod.BgMethodResult;
import com.hktcode.bgmethod.BgMethodPst;
import com.hktcode.bgmethod.BgMethodPstResult;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.bgtriple.result.TriplePstBgResult;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class TriplePstBgFuture //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    implements TripleBasicBgFuture<TriplePstBgResult<C, J, P>, C, J, P>
{
    public static < C extends TripleConsumer<C, J, P> //
        /*      */, J extends TripleJunction<C, J, P> //
        /*      */, P extends TripleProducer<C, J, P> //
        /*      */>
    TriplePstBgFuture<C, J, P> of //
        /* */(AtomicReference<BgMethodPst<C>> consumer //
        /* */, AtomicReference<BgMethodPst<J>> junction //
        /* */, AtomicReference<BgMethodPst<P>> producer //
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
        return new TriplePstBgFuture<>(consumer, junction, producer);
    }

    private final AtomicReference<BgMethodPst<C>> consumer;

    private final AtomicReference<BgMethodPst<J>> junction;

    private final AtomicReference<BgMethodPst<P>> producer;

    private TriplePstBgFuture //
        /* */(AtomicReference<BgMethodPst<C>> consumer //
        /* */, AtomicReference<BgMethodPst<J>> junction //
        /* */, AtomicReference<BgMethodPst<P>> producer //
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
    public TriplePstBgResult<C, J, P> get() //
        throws InterruptedException, ExecutionException
    {
        BgMethodPst<C> c;
        while (!((c = this.consumer.get()) instanceof BgMethodPstResult)) {
            Thread.sleep(10);
        }
        BgMethodPst<J> j;
        while (!((j = this.junction.get()) instanceof BgMethodPstResult)) {
            Thread.sleep(10);
        }
        BgMethodPst<P> p;
        while (!((p = this.producer.get()) instanceof BgMethodPstResult)) {
            Thread.sleep(10);
        }
        return TriplePstBgResult.of //
            /*     */( (BgMethodPstResult<C>) c //
                /* */, (BgMethodPstResult<J>) j //
                /* */, (BgMethodPstResult<P>) p //
                /* */);
    }

    @Override
    public TriplePstBgResult<C, J, P> get(long timeout, TimeUnit unit) //
        throws InterruptedException, ExecutionException, TimeoutException
    {
        if (unit == null) {
            throw new ArgumentNullException("unit");
        }
        long duration = unit.toMillis(timeout);
        long start = System.currentTimeMillis();
        BgMethodPst<C> c;
        while (!((c = this.consumer.get()) instanceof BgMethodPstResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        BgMethodPst<J> j;
        while (!((j = this.junction.get()) instanceof BgMethodPstResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        BgMethodPst<P> p;
        while (!((p = this.producer.get()) instanceof BgMethodPstResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        return TriplePstBgResult.of //
            /*     */( (BgMethodPstResult<C>) c //
                /* */, (BgMethodPstResult<J>) j //
                /* */, (BgMethodPstResult<P>) p //
                /* */);
    }
}

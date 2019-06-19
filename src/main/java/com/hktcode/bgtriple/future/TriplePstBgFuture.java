/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgtriple.future;

import com.hktcode.bgsimple.SimpleBasicBgResult;
import com.hktcode.bgsimple.SimpleBasicPstBgMethod;
import com.hktcode.bgsimple.SimpleBasicPstBgResult;
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
        /* */( AtomicReference<SimpleBasicPstBgMethod<C>> consumer //
        /* */, AtomicReference<SimpleBasicPstBgMethod<J>> junction //
        /* */, AtomicReference<SimpleBasicPstBgMethod<P>> producer //
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

    private final AtomicReference<SimpleBasicPstBgMethod<C>> consumer;

    private final AtomicReference<SimpleBasicPstBgMethod<J>> junction;

    private final AtomicReference<SimpleBasicPstBgMethod<P>> producer;

    private TriplePstBgFuture //
        /* */( AtomicReference<SimpleBasicPstBgMethod<C>> consumer //
        /* */, AtomicReference<SimpleBasicPstBgMethod<J>> junction //
        /* */, AtomicReference<SimpleBasicPstBgMethod<P>> producer //
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
        return consumer.get() instanceof SimpleBasicBgResult
            && junction.get() instanceof SimpleBasicBgResult
            && producer.get() instanceof SimpleBasicBgResult;
    }

    @Override
    public TriplePstBgResult<C, J, P> get() //
        throws InterruptedException, ExecutionException
    {
        SimpleBasicPstBgMethod<C> c;
        while (!((c = this.consumer.get()) instanceof SimpleBasicPstBgResult)) {
            Thread.sleep(10);
        }
        SimpleBasicPstBgMethod<J> j;
        while (!((j = this.junction.get()) instanceof SimpleBasicPstBgResult)) {
            Thread.sleep(10);
        }
        SimpleBasicPstBgMethod<P> p;
        while (!((p = this.producer.get()) instanceof SimpleBasicPstBgResult)) {
            Thread.sleep(10);
        }
        return TriplePstBgResult.of //
            /*     */( (SimpleBasicPstBgResult<C>) c //
                /* */, (SimpleBasicPstBgResult<J>) j //
                /* */, (SimpleBasicPstBgResult<P>) p //
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
        SimpleBasicPstBgMethod<C> c;
        while (!((c = this.consumer.get()) instanceof SimpleBasicPstBgResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        SimpleBasicPstBgMethod<J> j;
        while (!((j = this.junction.get()) instanceof SimpleBasicPstBgResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        SimpleBasicPstBgMethod<P> p;
        while (!((p = this.producer.get()) instanceof SimpleBasicPstBgResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        return TriplePstBgResult.of //
            /*     */( (SimpleBasicPstBgResult<C>) c //
                /* */, (SimpleBasicPstBgResult<J>) j //
                /* */, (SimpleBasicPstBgResult<P>) p //
                /* */);
    }
}

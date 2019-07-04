/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.future;

import com.hktcode.bgmethod.SimpleBasicBgResult;
import com.hktcode.bgmethod.SimpleBasicGetBgMethod;
import com.hktcode.bgmethod.SimpleBasicGetBgResult;
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
        /* */( AtomicReference<SimpleBasicGetBgMethod<C>> consumer //
        /* */, AtomicReference<SimpleBasicGetBgMethod<J>> junction //
        /* */, AtomicReference<SimpleBasicGetBgMethod<P>> producer //
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

    private final AtomicReference<SimpleBasicGetBgMethod<C>> consumer;

    private final AtomicReference<SimpleBasicGetBgMethod<J>> junction;

    private final AtomicReference<SimpleBasicGetBgMethod<P>> producer;

    private TripleGetBgFuture //
        /* */( AtomicReference<SimpleBasicGetBgMethod<C>> consumer //
        /* */, AtomicReference<SimpleBasicGetBgMethod<J>> junction //
        /* */, AtomicReference<SimpleBasicGetBgMethod<P>> producer //
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
    public TripleGetBgResult<C, J, P> get() throws InterruptedException, ExecutionException
    {
        SimpleBasicGetBgMethod<C> c;
        while (!((c = this.consumer.get()) instanceof SimpleBasicGetBgResult)) {
            Thread.sleep(10);
        }
        SimpleBasicGetBgMethod<J> j;
        while (!((j = this.junction.get()) instanceof SimpleBasicGetBgResult)) {
            Thread.sleep(10);
        }
        SimpleBasicGetBgMethod<P> p;
        while (!((p = this.producer.get()) instanceof SimpleBasicGetBgResult)) {
            Thread.sleep(10);
        }
        return TripleGetBgResult.of //
            /*     */( (SimpleBasicGetBgResult<C>) c //
                /* */, (SimpleBasicGetBgResult<J>) j //
                /* */, (SimpleBasicGetBgResult<P>) p //
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
        SimpleBasicGetBgMethod<C> c;
        while (!((c = this.consumer.get()) instanceof SimpleBasicGetBgResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        SimpleBasicGetBgMethod<J> j;
        while (!((j = this.junction.get()) instanceof SimpleBasicGetBgResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        SimpleBasicGetBgMethod<P> p;
        while (!((p = this.producer.get()) instanceof SimpleBasicGetBgResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        return TripleGetBgResult.of //
            /*     */( (SimpleBasicGetBgResult<C>) c //
                /* */, (SimpleBasicGetBgResult<J>) j //
                /* */, (SimpleBasicGetBgResult<P>) p //
                /* */);
    }
}

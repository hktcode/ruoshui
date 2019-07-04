/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgtriple.future;

import com.hktcode.bgmethod.BgMethodResult;
import com.hktcode.bgmethod.BgMethodPut;
import com.hktcode.bgmethod.BgMethodPutResult;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.bgtriple.result.TriplePutBgResult;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class TriplePutBgFuture //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    implements TripleBasicBgFuture<TriplePutBgResult<C, J, P>, C, J, P>
{
    public static < C extends TripleConsumer<C, J, P> //
        /*      */, J extends TripleJunction<C, J, P> //
        /*      */, P extends TripleProducer<C, J, P> //
        /*      */>
    TriplePutBgFuture<C, J, P> of //
        /* */(AtomicReference<BgMethodPut<C>> consumer //
        /* */, AtomicReference<BgMethodPut<J>> junction //
        /* */, AtomicReference<BgMethodPut<P>> producer //
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
        return new TriplePutBgFuture<>(consumer, junction, producer);
    }

    private final AtomicReference<BgMethodPut<C>> consumer;

    private final AtomicReference<BgMethodPut<J>> junction;

    private final AtomicReference<BgMethodPut<P>> producer;

    private TriplePutBgFuture //
        /* */(AtomicReference<BgMethodPut<C>> consumer //
        /* */, AtomicReference<BgMethodPut<J>> junction //
        /* */, AtomicReference<BgMethodPut<P>> producer //
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
    public TriplePutBgResult<C, J, P> get() //
        throws InterruptedException, ExecutionException
    {
        BgMethodPut<C> c;
        while (!((c = this.consumer.get()) instanceof BgMethodPutResult)) {
            Thread.sleep(10);
        }
        BgMethodPut<J> j;
        while (!((j = this.junction.get()) instanceof BgMethodPutResult)) {
            Thread.sleep(10);
        }
        BgMethodPut<P> p;
        while (!((p = this.producer.get()) instanceof BgMethodPutResult)) {
            Thread.sleep(10);
        }
        return TriplePutBgResult.of //
            /*     */( (BgMethodPutResult<C>) c //
                /* */, (BgMethodPutResult<J>) j //
                /* */, (BgMethodPutResult<P>) p //
                /* */);
    }

    @Override
    public TriplePutBgResult<C, J, P> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        if (unit == null) {
            throw new ArgumentNullException("unit");
        }
        long duration = unit.toMillis(timeout);
        long start = System.currentTimeMillis();
        BgMethodPut<C> c;
        while (!((c = this.consumer.get()) instanceof BgMethodPutResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        BgMethodPut<J> j;
        while (!((j = this.junction.get()) instanceof BgMethodPutResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        BgMethodPut<P> p;
        while (!((p = this.producer.get()) instanceof BgMethodPutResult)) {
            Thread.sleep(10);
            long current = System.currentTimeMillis();
            if (current - start >= duration) {
                throw new TimeoutException();
            }
        }
        return TriplePutBgResult.of //
            /*     */( (BgMethodPutResult<C>) c //
                /* */, (BgMethodPutResult<J>) j //
                /* */, (BgMethodPutResult<P>) p //
            );
    }
}

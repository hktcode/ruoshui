/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.status;

import com.hktcode.bgsimple.SimpleBasicBgMethod;
import com.hktcode.bgtriple.TripleBgWorker;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.bgtriple.future.TripleBasicBgFuture;
import com.hktcode.bgtriple.result.TripleBasicBgResult;
import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TripleOuterBgStatus //
    /* */< CM extends SimpleBasicBgMethod<C>
    /* */, JM extends SimpleBasicBgMethod<J>
    /* */, PM extends SimpleBasicBgMethod<P>
    /* */, C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */, R extends TripleBasicBgResult<C, J, P> //
    /* */> //
    implements TripleBasicBgStatus<C, J, P>
{
    public final AtomicReference<CM> consumer;

    public final AtomicReference<JM> junction;

    public final AtomicReference<PM> producer;

    protected TripleOuterBgStatus(CM consumer, JM junction, PM producer)
    {
        this.consumer = new AtomicReference<>(consumer);
        this.junction = new AtomicReference<>(junction);
        this.producer = new AtomicReference<>(producer);
    }

    public abstract TripleBasicBgFuture<R, C, J, P> newFuture();

    @Override
    public TripleBasicBgStatus<C, J, P> get(TripleGetBgStatus<C, J, P> get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 检查get的method中不是BasicGetBgResult
        return this;
    }

    @Override
    public TripleBasicBgStatus<C, J, P> pst(TriplePstBgStatus<C, J, P> pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 检查pst的method中不是BasicPstBgResult
        return this;
    }

    @Override
    public TripleBasicBgStatus<C, J, P> del(TripleDelBgStatus<C, J, P> del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 检查del的method中不是BasicDelBgResult
        return this;
    }

    @Override
    public TripleBasicBgStatus<C, J, P> //
    newStatus(TripleBgWorker<C, J, P> worker) throws InterruptedException
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.reference(this.consumer, this.junction, this.producer);
    }

    @Override
    public TripleBasicBgStatus<C, J, P> //
    newStatus(TripleBgWorker<C, J, P> worker, Throwable reasons, ZonedDateTime endtime) throws InterruptedException
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return worker.reference(this.consumer, this.junction, this.producer, reasons, endtime);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TripleBasicBgStatus<C, J, P> end(TripleEndBgStatus<C, J, P> end)
    {
        if (end == null) {
            throw new ArgumentNullException("end");
        }
        this.consumer.set((CM)end.consumer);
        this.junction.set((JM)end.junction);
        this.producer.set((PM)end.producer);
        return end;
    }
}

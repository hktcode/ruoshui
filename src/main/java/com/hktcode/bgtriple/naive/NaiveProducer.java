/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.naive;

import com.hktcode.bgmethod.*;
import com.hktcode.bgtriple.TripleBasicBgWorker;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.bgtriple.status.TripleDelBgStatus;
import com.hktcode.bgtriple.status.TripleEndBgStatus;
import com.hktcode.lang.RunnableWithInterrupted;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class NaiveProducer //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends NaiveProducer<C, J, P, F, M, O> //
    /* */, F extends NaiveProducerConfig //
    /* */, M extends NaiveProducerMutableMetric //
    /* */, O
    /* */> //
    extends TripleBasicBgWorker<C, J, P> //
    implements TripleProducer<C, J, P>, RunnableWithInterrupted
{
    private static final Logger logger = LoggerFactory.getLogger(NaiveProducer.class);

    public final F config;

    public final M metric;

    public final BlockingQueue<O> getout;

    protected NaiveProducer //
        /* */(F config //
        /* */, M metric
        /* */, BlockingQueue<O> getout //
        /* */, AtomicReference<TripleBasicBgStatus<C, J, P>> status //
        /* */)
    {
        super(status);
        this.config = config;
        this.metric = metric;
        this.getout = getout;
    }

    public O poll() throws InterruptedException
    {
        return Naive.poll(this.config, this.metric, this.getout);
    }

    @Override
    public BgMethodPutResultSuccess<F, P> put()
    {
        return BgMethodPutResultSuccess.of(this.metric.startMillis, this.config);
    }

    @Override
    public BgMethodResultMiscarried<P> put(Throwable reasons, ZonedDateTime endtime)
    {
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return BgMethodResultMiscarried.of(endtime);
    }

    @Override
    public BgMethodResultNormalInfo<F, ? extends NaiveProducerMetric, P> get()
    {
        return BgMethodResultNormalInfo.of(config, this.metric.toMetric());
    }

    @Override
    public BgMethodResultEndFailure<F, ? extends NaiveProducerMetric, P> get(Throwable reasons, ZonedDateTime endtime)
    {
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return BgMethodResultEndFailure.of(reasons, config, this.metric.toMetric(), endtime);
    }

    @Override
    public BgMethodResultEndFailure<F, ? extends NaiveProducerMetric, P> pst(Throwable reasons, ZonedDateTime endtime)
    {
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return BgMethodResultEndFailure.of(reasons, config, this.metric.toMetric(), endtime);
    }

    @Override
    public BgMethodResultEndSuccess<F, ? extends NaiveProducerMetric, P> del()
    {
        ZonedDateTime endtime = ZonedDateTime.now();
        return BgMethodResultEndSuccess.of(config, this.metric.toMetric(), endtime);
    }

    @Override
    public BgMethodResultEndFailure<F, ? extends NaiveProducerMetric, P>
    del(Throwable reasons, ZonedDateTime endtime)
    {
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return BgMethodResultEndFailure.of(reasons, config, metric.toMetric(), endtime);
    }

    protected abstract void runInternal() throws Exception;

    @Override
    public void runWithInterrupted() throws InterruptedException
    {
        try {
            this.runInternal();
            logger.info("naive producer finish: name={}", Thread.currentThread().getName());
        }
        catch (Exception ex) {
            ZonedDateTime endtime = ZonedDateTime.now();
            logger.error("naive producer throws exception: name={}", Thread.currentThread().getName(), ex);
            BgMethodDelParamsDefault<C> c = BgMethodDelParamsDefault.of();
            BgMethodDelParamsDefault<J> j = BgMethodDelParamsDefault.of();
            BgMethodResultEndFailure<F, NaiveProducerMetric, P> p //
                = BgMethodResultEndFailure.of(ex, this.config, this.metric.toMetric(), ZonedDateTime.now());
            TripleDelBgStatus<C, J, P> del = TripleDelBgStatus.of(c, j, p);
            TripleBasicBgStatus<C, J, P> origin;
            TripleBasicBgStatus<C, J, P> future;
            while (!((origin = super.newStatus(ex, endtime)) instanceof TripleEndBgStatus)) {
                future = origin.del(del);
                this.status.compareAndSet(origin, future);
            }
            logger.info("naive producer finish by exception.");
        }
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.naive;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgmethod.*;
import com.hktcode.bgtriple.TripleBasicBgWorker;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.bgtriple.status.TripleDelBgStatus;
import com.hktcode.bgtriple.status.TripleEndBgStatus;
import com.hktcode.bgtriple.status.TripleRunBgStatus;
import com.hktcode.lang.RunnableWithInterrupted;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class NaiveJunction //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends NaiveJunction<C, J, P, F, M, I, O> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */, F extends NaiveJunctionConfig //
    /* */, M extends NaiveJunctionMutableMetric
    /* */, I //
    /* */, O //
    /* */> //
    extends TripleBasicBgWorker<C, J, P> //
    implements TripleJunction<C, J, P>, RunnableWithInterrupted
{
    private static final Logger logger = LoggerFactory.getLogger(NaiveJunction.class);

    protected NaiveJunction //
        /* */( F config
        /* */, M metric
        /* */, BlockingQueue<I> comein
        /* */, BlockingQueue<O> getout
        /* */, AtomicReference<TripleBasicBgStatus<C, J, P>> status
        /* */)
    {
        super(status);
        this.comein = comein;
        this.getout = getout;
        this.config = config;
        this.metric = metric;
    }

    protected final BlockingQueue<I> comein;

    protected final BlockingQueue<O> getout;

    protected final F config;

    protected final M metric;

    protected I poll(BlockingQueue<I> queue) throws InterruptedException
    {
        return Naive.poll(this.config, this.metric, queue);
    }

    protected O push(O r) throws InterruptedException
    {
        return Naive.push(r, this.config, this.metric, this.getout);
    }

    @Override
    public BgMethodPutResultSuccess<F, J> put()
    {
        return BgMethodPutResultSuccess.of(this.metric.startMillis, this.config);
    }

    @Override
    public BgMethodResultMiscarried<J> put(Throwable reasons, ZonedDateTime endtime)
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
    public BgMethodResultEndFailure<F, NaiveJunctionMetric, J> //
    pst(Throwable reasons, ZonedDateTime endtime)
    {
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        NaiveJunctionMetric metrics = NaiveJunctionMetric.of(this.metric);
        return BgMethodResultEndFailure.of(reasons, config, metrics, endtime);
    }

    @Override
    public BgMethodResultNormalInfo<F, NaiveJunctionMetric, J> get()
    {
        NaiveJunctionMetric metrics = NaiveJunctionMetric.of(this.metric);
        return BgMethodResultNormalInfo.of(config, metrics);
    }

    @Override
    public BgMethodResultEndFailure<F, NaiveJunctionMetric, J> //
    get(Throwable reasons, ZonedDateTime endtime)
    {
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        NaiveJunctionMetric metrics = NaiveJunctionMetric.of(this.metric);
        return BgMethodResultEndFailure.of(reasons, config, metrics, endtime);
    }


    @Override
    public BgMethodResultEndSuccess<F, NaiveJunctionMetric, J> del()
    {
        ZonedDateTime endtime = ZonedDateTime.now();
        NaiveJunctionMetric metrics = NaiveJunctionMetric.of(this.metric);
        return BgMethodResultEndSuccess.of(config, metrics, endtime);
    }

    @Override
    public BgMethodResultEndFailure<F, NaiveJunctionMetric, J> //
    del(Throwable reasons, ZonedDateTime endtime)
    {
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        NaiveJunctionMetric metrics = NaiveJunctionMetric.of(this.metric);
        return BgMethodResultEndFailure.of(reasons, config, metrics, endtime);
    }

    @Override
    public void runWithInterrupted() throws InterruptedException
    {
        I r = null;
        O o = null;
        Iterator<O> t = ImmutableList.<O>of().iterator();
        try {
            while (super.newStatus() instanceof TripleRunBgStatus) {
                if (o != null) {
                    o = this.push(o);
                }
                else if (t.hasNext()) {
                    o = t.next();
                }
                else if (r == null) {
                    r = this.poll(this.comein);
                }
                else {
                    t = this.convert(r).iterator();
                    r = null;
                }
            }
            logger.info("junction finish.");
        }
        catch (Exception ex) {
            ZonedDateTime endtime = ZonedDateTime.now();
            logger.error("junction throws exception.", ex);
            NaiveJunctionMetric metrics = NaiveJunctionMetric.of(this.metric);
            BgMethodDelParamsDefault<C> c = BgMethodDelParamsDefault.of();
            BgMethodResultEndFailure<F, NaiveJunctionMetric, J> j = BgMethodResultEndFailure.of(ex, this.config, metrics, ZonedDateTime.now());
            BgMethodDelParamsDefault<P> p = BgMethodDelParamsDefault.of();
            TripleDelBgStatus<C, J, P> del = TripleDelBgStatus.of(c, j, p);
            TripleBasicBgStatus<C, J, P> origin;
            TripleBasicBgStatus<C, J, P> future;
            while (!((origin = super.newStatus(ex, endtime)) instanceof TripleEndBgStatus)) {
                future = origin.del(del);
                this.status.compareAndSet(origin, future);
            }
            logger.info("junction finish by exception.");
        }
    }

    protected abstract List<O> convert(I record);
}

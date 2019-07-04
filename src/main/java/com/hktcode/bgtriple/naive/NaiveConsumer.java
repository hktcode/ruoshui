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

public abstract class NaiveConsumer //
    /* */< C extends NaiveConsumer<C, J, P, F, M, I> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */, F extends NaiveConsumerConfig //
    /* */, M extends NaiveConsumerMutableMetric //
    /* */, I //
    /* */>
    extends TripleBasicBgWorker<C, J, P>
    implements TripleConsumer<C, J, P>, RunnableWithInterrupted
{
    private static final Logger logger = LoggerFactory.getLogger(NaiveConsumer.class);

    public final F config;

    public final BlockingQueue<I> comein;

    public M metric; // TODO: 废弃该成员变量

    protected NaiveConsumer //
        /* */(F config //
        /* */, M metric //
        /* */, BlockingQueue<I> comein //
        /* */, AtomicReference<TripleBasicBgStatus<C, J, P>> status //
        /* */)
    {
        super(status);
        this.config = config;
        this.metric = metric;
        this.comein = comein;
    }

    protected abstract void runInternal() throws Exception;

    @Override
    public void runWithInterrupted() throws InterruptedException
    {
        try {
            this.runInternal();
            logger.info("consumer finish.");
        }
        catch (Exception ex) {
            logger.error("consumer throws exception: ", ex);
            ZonedDateTime endtime = ZonedDateTime.now();
            String msg = ex.getMessage();
            metric.statusInfor = "throw exception at " + endtime + ": " + msg;
            BgMethodResultEndFailure<F, NaiveConsumerMetric, C> c //
                = BgMethodResultEndFailure.of(ex, this.config, this.metric.toMetric(), ZonedDateTime.now());
            BgMethodDelParamsDefault<J> j = BgMethodDelParamsDefault.of();
            BgMethodDelParamsDefault<P> p = BgMethodDelParamsDefault.of();
            TripleDelBgStatus<C, J, P> del = TripleDelBgStatus.of(c, j, p);
            TripleBasicBgStatus<C, J, P> origin;
            TripleBasicBgStatus<C, J, P> future;
            while (!((origin = super.newStatus(ex, endtime)) instanceof TripleEndBgStatus)) {
                future = origin.del(del);
                this.status.compareAndSet(origin, future);
                metric.statusInfor = "waiting status set to end";
            }
            metric.statusInfor = "consumer finish end";
            logger.info("consumer finish by exception.");
        }
    }

    @Override
    public BgMethodResultEndFailure<F, NaiveConsumerMetric, C> //
    pst(Throwable reasons, ZonedDateTime endtime)
    {
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        return BgMethodResultEndFailure.of(reasons, config, metric.toMetric(), endtime);
    }

    @Override
    public BgMethodPutResultSuccess<F, C> put()
    {
        return BgMethodPutResultSuccess.of(metric.startMillis, config);
    }

    @Override
    public BgMethodResultEndFailure<F, NaiveConsumerMetric, C> //
    put(Throwable reasons, ZonedDateTime endtime)
    {
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        return BgMethodResultEndFailure.of(reasons, config, metric.toMetric(), endtime);
    }

    @Override
    public BgMethodResultNormalInfo<F, NaiveConsumerMetric, C> get()
    {
        return BgMethodResultNormalInfo.of(config, metric.toMetric());
    }

    @Override
    public BgMethodResultEndFailure<F, NaiveConsumerMetric, C> //
    get(Throwable reasons, ZonedDateTime endtime)
    {
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        return BgMethodResultEndFailure.of(reasons, config, metric.toMetric(), endtime);
    }

    @Override
    public BgMethodResultEndSuccess<F, NaiveConsumerMetric, C> del()
    {
        ZonedDateTime endtime = ZonedDateTime.now();
        return BgMethodResultEndSuccess.of(config, metric.toMetric(), endtime);
    }

    @Override
    public BgMethodResultEndFailure<F, NaiveConsumerMetric, C> //
    del(Throwable reasons, ZonedDateTime endtime)
    {
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        return BgMethodResultEndFailure.of(reasons, config, metric.toMetric(), endtime);
    }

    public I push(I record) throws InterruptedException
    {
        return Naive.push(record, this.config, this.metric, this.comein);
    }
}

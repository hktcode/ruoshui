/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TripleProducer //
    /* */< W extends TripleProducer<W, F, M, O> //
    /* */, F extends TripleProducerConfig //
    /* */, M extends TripleProducerMetric //
    /* */, O
    /* */> //
    extends TripleWorker<W, F, M> //
{
    public final BlockingQueue<O> getout;

    protected TripleProducer //
        /* */( F config //
        /* */, BlockingQueue<O> getout //
        /* */, AtomicReference<SimpleStatus<W, M>> status //
        /* */)
    {
        super(config, status, 2);
        this.getout = getout;
    }

    public O poll(M metric) throws InterruptedException
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return Triple.poll(this.config, metric, this.getout);
    }
}

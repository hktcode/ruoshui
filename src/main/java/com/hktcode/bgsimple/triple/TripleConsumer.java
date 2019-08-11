/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.status.SimpleStatus;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TripleConsumer //
    /* */< W extends TripleConsumer<W, F, M, I> //
    /* */, F extends TripleConsumerConfig //
    /* */, M extends TripleConsumerMetric //
    /* */, I //
    /* */>
    extends TripleWorker<W, F, M>
{
    protected final BlockingQueue<I> comein;

    protected final M metric;

    protected TripleConsumer //
        /* */( F config //
        /* */, M metric //
        /* */, BlockingQueue<I> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        super(config, status,0);
        this.comein = comein;
        this.metric = metric;
    }

    public I push(I record) throws InterruptedException
    {
        return Triple.push(record, this.config, this.metric, this.comein);
    }
}

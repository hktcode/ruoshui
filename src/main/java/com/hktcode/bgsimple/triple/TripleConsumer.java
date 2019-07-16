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

    protected TripleConsumer //
        /* */( F config //
        /* */, BlockingQueue<I> comein //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        super(config, status,0);
        this.comein = comein;
    }

    public I push(I record) throws InterruptedException
    {
        // TODO: return Triple.push(record, this.config, metric, this.comein);
        return Triple.push(record, this.config, null, this.comein);
    }
}

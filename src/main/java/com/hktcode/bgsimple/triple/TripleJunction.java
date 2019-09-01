/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TripleJunction //
    /* */< W extends TripleJunction<W, F, M, I, O> //
    /* */, F extends TripleJunctionConfig //
    /* */, M extends TripleJunctionMetric
    /* */, I //
    /* */, O //
    /* */> //
    extends TripleWorker<W, F, M> //
{
    private static final Logger logger = LoggerFactory.getLogger(TripleJunction.class);

    protected final BlockingQueue<I> comein;

    protected final BlockingQueue<O> getout;

    protected TripleJunction //
        /* */( F config
        /* */, BlockingQueue<I> comein
        /* */, BlockingQueue<O> getout
        /* */, AtomicReference<SimpleStatus> status
        /* */)
    {
        super(config, status, 1);
        this.comein = comein;
        this.getout = getout;
    }

    // TODO:
    TripleJunctionMetric metric = TripleJunctionMetric.of(ZonedDateTime.now());

    protected I poll(BlockingQueue<I> queue) throws InterruptedException
    {
        if (queue == null) {
            throw new ArgumentNullException("queue");
        }
        return Triple.poll(this.config, metric, queue);
    }

    protected O push(O record) throws InterruptedException
    {
        if (record == null) {
            throw new ArgumentNullException("record");
        }
        return Triple.push(record, this.config, metric, this.getout);
    }

    public void runInternal(W worker) throws InterruptedException
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        I r = null;
        O o = null;
        Iterator<O> t = ImmutableList.<O>of().iterator();
        while (super.newStatus(worker) instanceof SimpleStatusInnerRun) {
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
                t = this.convert(r, worker).iterator();
                r = null;
            }
        }
        logger.info("junction finish.");
    }

    protected abstract List<O> convert(I record, W worker);
}

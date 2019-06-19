/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgtriple.status;

import com.hktcode.bgtriple.TripleBgWorker;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public interface TripleInnerBgStatus
    /* */< S extends TripleConsumer<S, P, D> //
    /* */, P extends TripleJunction<S, P, D> //
    /* */, D extends TripleProducer<S, P, D> //
    /* */> //
    extends TripleBasicBgStatus<S, P, D>
{
    @Override
    default TripleInnerBgStatus<S, P, D> //
    newStatus(TripleBgWorker<S, P, D> worker) throws InterruptedException
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return this;
    }

    @Override
    default TripleBasicBgStatus<S, P, D> end(TripleEndBgStatus<S, P, D> end)
    {
        if (end == null) {
            throw new ArgumentNullException("end");
        }
        return end;
    }

    @Override
    default TripleInnerBgStatus<S, P, D> //
    newStatus(TripleBgWorker<S, P, D> worker, Throwable reasons, ZonedDateTime endtime) //
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return this;
    }
}

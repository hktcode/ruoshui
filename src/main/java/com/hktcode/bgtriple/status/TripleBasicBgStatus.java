/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgtriple.status;

import com.hktcode.bgtriple.TripleBgWorker;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;

import java.time.ZonedDateTime;

public interface TripleBasicBgStatus //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
{
    TripleBasicBgStatus<C, J, P> get(TripleGetBgStatus<C, J, P> get);

    TripleBasicBgStatus<C, J, P> pst(TriplePstBgStatus<C, J, P> pst);

    TripleBasicBgStatus<C, J, P> del(TripleDelBgStatus<C, J, P> del);

    TripleBasicBgStatus<C, J, P> end(TripleEndBgStatus<C, J, P> end);

    TripleBasicBgStatus<C, J, P> //
    newStatus(TripleBgWorker<C, J, P> worker) throws InterruptedException;

    TripleBasicBgStatus<C, J, P> //
    newStatus(TripleBgWorker<C, J, P> worker, Throwable reasons, ZonedDateTime endtime) //
        throws InterruptedException;
}

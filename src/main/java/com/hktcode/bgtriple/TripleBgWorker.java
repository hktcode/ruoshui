/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple;

import com.hktcode.bgsimple.SimpleBasicBgMethod;
import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.bgtriple.status.TripleInnerBgStatus;

import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicReference;

public interface TripleBgWorker //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    extends Runnable
{
    TripleSwitcher<C, J, P> switcher();

    TripleInnerBgStatus<C, J, P> newStatus() throws InterruptedException;

    TripleInnerBgStatus<C, J, P> newStatus(Throwable reasons, ZonedDateTime endtime) throws InterruptedException;

    public
        /* */< SM extends SimpleBasicBgMethod<C> //
        /* */, PM extends SimpleBasicBgMethod<J> //
        /* */, DM extends SimpleBasicBgMethod<P> //
        /* */> //
    TripleBasicBgStatus<C, J, P> //
    reference(AtomicReference<SM> consumer //
        /* */, AtomicReference<PM> junction //
        /* */, AtomicReference<DM> producer //
        /* */) throws InterruptedException;

    public
        /* */< SM extends SimpleBasicBgMethod<C> //
        /* */, PM extends SimpleBasicBgMethod<J> //
        /* */, DM extends SimpleBasicBgMethod<P> //
        /* */> //
    TripleBasicBgStatus<C, J, P> //
    reference(AtomicReference<SM> consumer //
        /* */, AtomicReference<PM> junction //
        /* */, AtomicReference<DM> producer //
        /* */, Throwable reasons //
        /* */, ZonedDateTime endtime //
        /* */) throws InterruptedException;
}

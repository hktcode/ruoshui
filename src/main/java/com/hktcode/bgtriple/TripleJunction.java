/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple;

import com.hktcode.bgsimple.SimpleBasicBgMethod;
import com.hktcode.bgsimple.SimpleBasicBgWorker;
import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicReference;

public interface TripleJunction //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    extends TripleBgWorker<C, J, P>, SimpleBasicBgWorker<J>
{
    Logger logger = LoggerFactory.getLogger(TripleJunction.class);

    @Override
    @SuppressWarnings("unchecked")
    default //
        /* */< SM extends SimpleBasicBgMethod<C> //
        /* */, PM extends SimpleBasicBgMethod<J> //
        /* */, DM extends SimpleBasicBgMethod<P> //
        /* */> //
    TripleBasicBgStatus<C, J, P>  //
    reference(AtomicReference<SM> consumer //
        /* */, AtomicReference<PM> junction //
        /* */, AtomicReference<DM> producer //
        /* */) throws InterruptedException //
    {
        if (consumer == null) {
            throw new ArgumentNullException("consumer");
        }
        if (junction == null) {
            throw new ArgumentNullException("junction");
        }
        if (producer == null) {
            throw new ArgumentNullException("producer");
        }
        PM origin = junction.get();
        SimpleBasicBgMethod<J> future = origin.run((J)this);
        while (!junction.compareAndSet(origin, (PM)future)) {
            logger.info("compare and set result fail");
            origin = junction.get();
            future = origin.run((J)this);
        }
        return TripleBasicBgWorker.newStatus(consumer, junction, producer);
    }

    @Override
    @SuppressWarnings("unchecked")
    default //
        /* */< SM extends SimpleBasicBgMethod<C> //
        /* */, PM extends SimpleBasicBgMethod<J> //
        /* */, DM extends SimpleBasicBgMethod<P> //
        /* */> //
    TripleBasicBgStatus<C, J, P>  //
    reference(AtomicReference<SM> consumer //
        /* */, AtomicReference<PM> junction //
        /* */, AtomicReference<DM> producer //
        /* */, Throwable reasons //
        /* */, ZonedDateTime endtime //
        /* */) throws InterruptedException //
    {
        if (consumer == null) {
            throw new ArgumentNullException("consumer");
        }
        if (junction == null) {
            throw new ArgumentNullException("junction");
        }
        if (producer == null) {
            throw new ArgumentNullException("producer");
        }
        PM origin = junction.get();
        SimpleBasicBgMethod<J> future = origin.run((J)this, reasons, endtime);
        while (!junction.compareAndSet(origin, (PM)future)) {
            logger.info("compare and set result fail");
            origin = junction.get();
            future = origin.run((J)this, reasons, endtime);
        }
        return TripleBasicBgWorker.newStatus(consumer, junction, producer);
    }
}

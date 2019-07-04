/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple;

import com.hktcode.bgmethod.BgMethod;
import com.hktcode.bgmethod.SimpleBgWorker;
import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicReference;

public interface TripleConsumer
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    extends TripleBgWorker<C, J, P>, SimpleBgWorker<C>
{
    Logger logger = LoggerFactory.getLogger(TripleConsumer.class);

    @Override
    @SuppressWarnings("unchecked")
    default //
        /* */< SM extends BgMethod<C> //
        /* */, PM extends BgMethod<J> //
        /* */, DM extends BgMethod<P> //
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
        SM origin = consumer.get();
        BgMethod<C> future = origin.run((C)this);
        while (!consumer.compareAndSet(origin, (SM)future)) {
            logger.info("compare and set result fail");
            origin = consumer.get();
            future = origin.run((C)this);
        }
        return TripleBasicBgWorker.newStatus(consumer, junction, producer);
    }

    @Override
    @SuppressWarnings("unchecked")
    default //
        /* */< SM extends BgMethod<C> //
        /* */, PM extends BgMethod<J> //
        /* */, DM extends BgMethod<P> //
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
        SM origin = consumer.get();
        BgMethod<C> future = origin.run((C)this, reasons, endtime);
        while (!consumer.compareAndSet(origin, (SM)future)) {
            logger.info("compare and set result fail");
            origin = consumer.get();
            future = origin.run((C)this, reasons, endtime);
        }
        return TripleBasicBgWorker.newStatus(consumer, junction, producer);
    }
}

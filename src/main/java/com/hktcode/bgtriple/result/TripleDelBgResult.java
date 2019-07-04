/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.result;

import com.hktcode.bgmethod.BgMethodDelResult;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.lang.exception.ArgumentNullException;

public class TripleDelBgResult //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    implements TripleBasicBgResult<C, J, P>
{
    public static < C extends TripleConsumer<C, J, P> //
        /*      */, J extends TripleJunction<C, J, P> //
        /*      */, P extends TripleProducer<C, J, P> //
        /*      */> //
    TripleDelBgResult<C, J, P> of //
        /* */(BgMethodDelResult<C> consumer //
        /* */, BgMethodDelResult<J> junction //
        /* */, BgMethodDelResult<P> producer //
        /* */) //
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
        return new TripleDelBgResult<>(consumer, junction, producer);
    }

    public final BgMethodDelResult<C> consumer;

    public final BgMethodDelResult<J> junction;

    public final BgMethodDelResult<P> producer;

    private TripleDelBgResult //
        /* */( BgMethodDelResult<C> consumer
        /* */, BgMethodDelResult<J> junction
        /* */, BgMethodDelResult<P> producer
        /* */)
    {
        this.consumer = consumer;
        this.junction = junction;
        this.producer = producer;
    }
}

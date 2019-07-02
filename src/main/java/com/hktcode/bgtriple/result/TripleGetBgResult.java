/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.result;

import com.hktcode.bgsimple.SimpleBasicGetBgResult;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.lang.exception.ArgumentNullException;

public class TripleGetBgResult //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    implements TripleBasicBgResult<C, J, P>
{
    public static < C extends TripleConsumer<C, J, P> //
        /*      */, J extends TripleJunction<C, J, P> //
        /*      */, P extends TripleProducer<C, J, P> //
        /*      */>
    TripleGetBgResult<C, J, P> of
        /* */( SimpleBasicGetBgResult<C> consumer //
        /* */, SimpleBasicGetBgResult<J> junction //
        /* */, SimpleBasicGetBgResult<P> producer //
        /* */)
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
        return new TripleGetBgResult<>(consumer, junction, producer);
    }

    public final SimpleBasicGetBgResult<C> consumer;

    public final SimpleBasicGetBgResult<J> junction;

    public final SimpleBasicGetBgResult<P> producer;

    private TripleGetBgResult //
        /* */( SimpleBasicGetBgResult<C> consumer //
        /* */, SimpleBasicGetBgResult<J> junction //
        /* */, SimpleBasicGetBgResult<P> producer //
        /* */)
    {
        this.consumer = consumer;
        this.junction = junction;
        this.producer = producer;
    }
}

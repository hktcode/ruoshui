/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.result;

import com.hktcode.bgsimple.SimpleBasicPutBgResult;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.lang.exception.ArgumentNullException;

public class TriplePutBgResult //
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
    TriplePutBgResult<C, J, P> of //
        /* */( SimpleBasicPutBgResult<C> consumer //
        /* */, SimpleBasicPutBgResult<J> junction //
        /* */, SimpleBasicPutBgResult<P> producer //
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
        return new TriplePutBgResult<>(consumer, junction, producer);
    }

    public final SimpleBasicPutBgResult<C> consumer;

    public final SimpleBasicPutBgResult<J> junction;

    public final SimpleBasicPutBgResult<P> producer;

    private TriplePutBgResult //
        /* */( SimpleBasicPutBgResult<C> consumer //
        /* */, SimpleBasicPutBgResult<J> junction //
        /* */, SimpleBasicPutBgResult<P> producer //
        /* */)
    {
        this.consumer = consumer;
        this.junction = junction;
        this.producer = producer;
    }
}

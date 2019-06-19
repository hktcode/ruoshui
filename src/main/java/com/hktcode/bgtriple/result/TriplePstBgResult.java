/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgtriple.result;

import com.hktcode.bgsimple.SimpleBasicPstBgResult;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.lang.exception.ArgumentNullException;

public class TriplePstBgResult //
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
    TriplePstBgResult<C, J, P> of //
        /* */( SimpleBasicPstBgResult<C> consumer //
        /* */, SimpleBasicPstBgResult<J> junction //
        /* */, SimpleBasicPstBgResult<P> producer //
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
        return new TriplePstBgResult<>(consumer, junction, producer);
    }

    public final SimpleBasicPstBgResult<C> consumer;

    public final SimpleBasicPstBgResult<J> junction;

    public final SimpleBasicPstBgResult<P> producer;

    protected TriplePstBgResult //
        /* */( SimpleBasicPstBgResult<C> consumer //
        /* */, SimpleBasicPstBgResult<J> junction //
        /* */, SimpleBasicPstBgResult<P> producer //
        /* */)
    {
        this.consumer = consumer;
        this.junction = junction;
        this.producer = producer;
    }
}

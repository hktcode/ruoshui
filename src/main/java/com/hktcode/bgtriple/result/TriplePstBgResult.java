/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.result;

import com.hktcode.bgmethod.BgMethodPstResult;
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
        /* */(BgMethodPstResult<C> consumer //
        /* */, BgMethodPstResult<J> junction //
        /* */, BgMethodPstResult<P> producer //
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

    public final BgMethodPstResult<C> consumer;

    public final BgMethodPstResult<J> junction;

    public final BgMethodPstResult<P> producer;

    protected TriplePstBgResult //
        /* */(BgMethodPstResult<C> consumer //
        /* */, BgMethodPstResult<J> junction //
        /* */, BgMethodPstResult<P> producer //
        /* */)
    {
        this.consumer = consumer;
        this.junction = junction;
        this.producer = producer;
    }
}

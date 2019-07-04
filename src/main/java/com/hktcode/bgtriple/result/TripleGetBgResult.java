/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.result;

import com.hktcode.bgmethod.BgMethodGetResult;
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
        /* */(BgMethodGetResult<C> consumer //
        /* */, BgMethodGetResult<J> junction //
        /* */, BgMethodGetResult<P> producer //
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

    public final BgMethodGetResult<C> consumer;

    public final BgMethodGetResult<J> junction;

    public final BgMethodGetResult<P> producer;

    private TripleGetBgResult //
        /* */(BgMethodGetResult<C> consumer //
        /* */, BgMethodGetResult<J> junction //
        /* */, BgMethodGetResult<P> producer //
        /* */)
    {
        this.consumer = consumer;
        this.junction = junction;
        this.producer = producer;
    }
}

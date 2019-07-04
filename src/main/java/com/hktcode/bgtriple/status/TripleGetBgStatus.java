/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.status;

import com.hktcode.bgmethod.BgMethodGet;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.bgtriple.future.TripleGetBgFuture;
import com.hktcode.bgtriple.result.TripleGetBgResult;
import com.hktcode.lang.exception.ArgumentNullException;

public class TripleGetBgStatus
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    extends TripleOuterBgStatus //
    /* */<BgMethodGet<C>
    /* */, BgMethodGet<J>
    /* */, BgMethodGet<P>
    /* */, C
    /* */, J
    /* */, P
    /* */, TripleGetBgResult<C, J, P>
    /* */>
{
    public static < C extends TripleConsumer<C, J, P> //
        /*      */, J extends TripleJunction<C, J, P> //
        /*      */, P extends TripleProducer<C, J, P> //
        /*      */>
    TripleGetBgStatus<C, J, P> of //
        /* */(BgMethodGet<C> consumer //
        /* */, BgMethodGet<J> junction //
        /* */, BgMethodGet<P> producer //
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
        return new TripleGetBgStatus<>(consumer, junction, producer);
    }

    private TripleGetBgStatus //
        /* */(BgMethodGet<C> consumer //
        /* */, BgMethodGet<J> junction //
        /* */, BgMethodGet<P> producer //
        /* */)
    {
        super(consumer, junction, producer);
    }

    @Override
    public TripleGetBgFuture<C, J, P> newFuture()
    {
        return TripleGetBgFuture.of(consumer, junction, producer);
    }
}

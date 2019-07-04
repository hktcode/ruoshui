/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.status;

import com.hktcode.bgmethod.BgMethodPst;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.bgtriple.future.TriplePstBgFuture;
import com.hktcode.bgtriple.result.TriplePstBgResult;
import com.hktcode.lang.exception.ArgumentNullException;

public class TriplePstBgStatus
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    extends TripleOuterBgStatus //
    /* */<BgMethodPst<C>
    /* */, BgMethodPst<J>
    /* */, BgMethodPst<P>
    /* */, C
    /* */, J
    /* */, P
    /* */, TriplePstBgResult<C, J, P>
    /* */>
{
    public static < C extends TripleConsumer<C, J, P> //
        /*      */, J extends TripleJunction<C, J, P> //
        /*      */, P extends TripleProducer<C, J, P> //
        /*      */>
    TriplePstBgStatus<C, J, P> of //
        /* */(BgMethodPst<C> consumer //
        /* */, BgMethodPst<J> junction //
        /* */, BgMethodPst<P> producer //
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
        return new TriplePstBgStatus<>(consumer, junction, producer);
    }

    private TriplePstBgStatus //
        /* */(BgMethodPst<C> consumer //
        /* */, BgMethodPst<J> junction //
        /* */, BgMethodPst<P> producer //
        /* */)
    {
        super(consumer, junction, producer);
    }

    @Override
    public TriplePstBgFuture<C, J, P> newFuture()
    {
        return TriplePstBgFuture.of(super.consumer, super.junction, super.producer);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.status;

import com.hktcode.bgsimple.SimpleBasicPutBgMethod;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.bgtriple.future.TriplePutBgFuture;
import com.hktcode.bgtriple.result.TriplePutBgResult;
import com.hktcode.lang.exception.ArgumentNullException;

public class TriplePutBgStatus
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    extends TripleOuterBgStatus //
    /* */< SimpleBasicPutBgMethod<C>
    /* */, SimpleBasicPutBgMethod<J>
    /* */, SimpleBasicPutBgMethod<P>
    /* */, C
    /* */, J
    /* */, P
    /* */, TriplePutBgResult<C, J, P>
    /* */>
{
    public static < C extends TripleConsumer<C, J, P> //
        /*      */, J extends TripleJunction<C, J, P> //
        /*      */, P extends TripleProducer<C, J, P> //
        /*      */>
    TriplePutBgStatus<C, J, P> of
        /* */( SimpleBasicPutBgMethod<C> consumer //
        /* */, SimpleBasicPutBgMethod<J> junction //
        /* */, SimpleBasicPutBgMethod<P> producer //
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
        return new TriplePutBgStatus<>(consumer, junction, producer);
    }

    private TriplePutBgStatus //
        /* */( SimpleBasicPutBgMethod<C> consumer //
        /* */, SimpleBasicPutBgMethod<J> junction //
        /* */, SimpleBasicPutBgMethod<P> producer //
        /* */)
    {
        super(consumer, junction, producer);
    }

    @Override
    public TriplePutBgFuture<C, J, P> newFuture()
    {
        return TriplePutBgFuture.of(consumer, junction, producer);
    }
}

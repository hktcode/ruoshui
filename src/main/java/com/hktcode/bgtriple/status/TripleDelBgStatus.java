/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.status;

import com.hktcode.bgsimple.SimpleBasicDelBgMethod;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.bgtriple.future.TripleDelBgFuture;
import com.hktcode.bgtriple.result.TripleDelBgResult;
import com.hktcode.lang.exception.ArgumentNullException;

public class TripleDelBgStatus
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    extends TripleOuterBgStatus //
    /* */< SimpleBasicDelBgMethod<C>
    /* */, SimpleBasicDelBgMethod<J>
    /* */, SimpleBasicDelBgMethod<P>
    /* */, C
    /* */, J
    /* */, P
    /* */, TripleDelBgResult<C, J, P>
    /* */>
{
    public static < C extends TripleConsumer<C, J, P> //
        /*      */, J extends TripleJunction<C, J, P> //
        /*      */, P extends TripleProducer<C, J, P> //
        /*      */>
    TripleDelBgStatus<C, J, P> of //
        /* */( SimpleBasicDelBgMethod<C> consumer //
        /* */, SimpleBasicDelBgMethod<J> junction //
        /* */, SimpleBasicDelBgMethod<P> producer //
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
        return new TripleDelBgStatus<>(consumer, junction, producer);
    }

    private TripleDelBgStatus //
        /* */( SimpleBasicDelBgMethod<C> consumer //
        /* */, SimpleBasicDelBgMethod<J> junction //
        /* */, SimpleBasicDelBgMethod<P> producer //
        /* */) //
    {
        super(consumer, junction, producer);
    }

    @Override
    public TripleDelBgFuture<C, J, P> newFuture()
    {
        return TripleDelBgFuture.of(consumer, junction, producer);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.status;

import com.hktcode.bgmethod.BgMethodDel;
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
    /* */<BgMethodDel<C>
    /* */, BgMethodDel<J>
    /* */, BgMethodDel<P>
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
        /* */(BgMethodDel<C> consumer //
        /* */, BgMethodDel<J> junction //
        /* */, BgMethodDel<P> producer //
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
        /* */(BgMethodDel<C> consumer //
        /* */, BgMethodDel<J> junction //
        /* */, BgMethodDel<P> producer //
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

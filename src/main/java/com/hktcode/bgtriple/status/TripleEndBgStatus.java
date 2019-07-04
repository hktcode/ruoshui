/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.status;

import com.hktcode.bgmethod.BgMethodResultEnd;
import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.lang.exception.ArgumentNullException;

public class TripleEndBgStatus
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    implements TripleInnerBgStatus<C, J, P>
{
    public static < C extends TripleConsumer<C, J, P> //
        /*      */, J extends TripleJunction<C, J, P> //
        /*      */, P extends TripleProducer<C, J, P> //
        /*      */>
    TripleEndBgStatus<C, J, P> of //
        /* */(BgMethodResultEnd<C> consumer //
        /* */, BgMethodResultEnd<J> junction //
        /* */, BgMethodResultEnd<P> producer //
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
        return new TripleEndBgStatus<>(consumer, junction, producer);
    }


    public final BgMethodResultEnd<C> consumer;
    public final BgMethodResultEnd<J> junction;
    public final BgMethodResultEnd<P> producer;

    private TripleEndBgStatus //
        /* */(BgMethodResultEnd<C> consumer //
        /* */, BgMethodResultEnd<J> junction //
        /* */, BgMethodResultEnd<P> producer //
        /* */)
    {
        this.consumer = consumer;
        this.junction = junction;
        this.producer = producer;
    }

    @Override
    public TripleGetBgStatus<C, J, P> get(TripleGetBgStatus<C, J, P> get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 检查get的method中不是BasicGetBgResult
        return TripleGetBgStatus.of(consumer, junction, producer);
    }

    @Override
    public TriplePstBgStatus<C, J, P> pst(TriplePstBgStatus<C, J, P> pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 检查pst的method中不是BasicPstBgResult
        return TriplePstBgStatus.of(consumer, junction, producer);
    }

    @Override
    public TripleDelBgStatus<C, J, P> del(TripleDelBgStatus<C, J, P> del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 检查del的method中不是BasicDelBgResult
        return TripleDelBgStatus.of(consumer, junction, producer);
    }
}

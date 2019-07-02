/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.status;

import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.TripleProducer;
import com.hktcode.lang.exception.ArgumentNullException;

public interface TripleRunBgStatus //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends TripleProducer<C, J, P> //
    /* */> //
    extends TripleInnerBgStatus<C, J, P>
{
    public static < C extends TripleConsumer<C, J, P> //
        /*      */, J extends TripleJunction<C, J, P> //
        /*      */, P extends TripleProducer<C, J, P> //
        /*      */> //
    TripleRunBgStatus<C, J, P> of()
    {
        return new TripleRunBgStatus<C, J, P>(){};
    }

    @Override
    default TripleBasicBgStatus<C, J, P> get(TripleGetBgStatus<C, J, P> get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 检查get的method中不是BasicGetBgResult
        return get;
    }

    @Override
    default TripleBasicBgStatus<C, J, P> pst(TriplePstBgStatus<C, J, P> pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 检查pst的method中不是BasicPstBgResult
        return pst;
    }

    @Override
    default TripleBasicBgStatus<C, J, P> del(TripleDelBgStatus<C, J, P> del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 检查del的method中不是BasicDelBgResult
        return del;
    }
}

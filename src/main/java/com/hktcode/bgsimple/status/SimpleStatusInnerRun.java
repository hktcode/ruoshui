/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleStatusInnerRun<W extends SimpleWorker<W, M>, M> //
    extends SimpleStatusInner<W, M>
{
    public static <W extends SimpleWorker<W, M>, M>
    SimpleStatusInnerRun<W, M> of()
    {
        return new SimpleStatusInnerRun<>();
    }

    public SimpleStatus<W, M> outer(SimpleStatus<W, M> outer)
    {
        if (outer == null) {
            throw new ArgumentNullException("outer");
        }
        return outer;
    }

    @Override
    public SimpleStatus<W, M> get(SimpleStatusOuterGet<W, M> get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 检查get的method中不是BasicGetBgResult
        return get;
    }

    @Override
    public SimpleStatus<W, M> pst(SimpleStatusOuterPst<W, M> pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 检查pst的method中不是BasicGetBgResult
        return pst;
    }

    @Override
    public SimpleStatus<W, M> del(SimpleStatusOuterDel<W, M> del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 检查del的method中不是BasicDelBgResult
        return del;
    }
}

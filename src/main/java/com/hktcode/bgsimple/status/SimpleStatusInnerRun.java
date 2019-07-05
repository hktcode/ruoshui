/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleStatusInnerRun extends SimpleStatusInner
{
    public static SimpleStatusInnerRun of()
    {
        return new SimpleStatusInnerRun();
    }

    public SimpleStatus outer(SimpleStatus outer)
    {
        if (outer == null) {
            throw new ArgumentNullException("outer");
        }
        return outer;
    }

    @Override
    public SimpleStatus get(SimpleStatusOuterGet get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 检查get的method中不是BasicGetBgResult
        return get;
    }

    @Override
    public SimpleStatus pst(SimpleStatusOuterPst pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 检查pst的method中不是BasicGetBgResult
        return pst;
    }

    @Override
    public SimpleStatus del(SimpleStatusOuterDel del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 检查del的method中不是BasicDelBgResult
        return del;
    }
}

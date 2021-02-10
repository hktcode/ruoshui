/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleStatusInnerRun implements SimpleStatusInner
{
    public static SimpleStatusInnerRun of()
    {
        return new SimpleStatusInnerRun();
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

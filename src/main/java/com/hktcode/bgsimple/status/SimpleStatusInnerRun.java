/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.SimpleMethodAllResultRun;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleStatusInnerRun extends SimpleStatusInner
{
    public static SimpleStatusInnerRun of(ImmutableList<SimpleMethodAllResultRun<?>> result)
    {
        if (result == null) {
            throw new ArgumentNullException("result");
        }
        return new SimpleStatusInnerRun(result);
    }

    private SimpleStatusInnerRun(ImmutableList<SimpleMethodAllResultRun<?>> result)
    {
        super(result);
    }

    @Override
    public SimpleStatusOuterGet get(SimpleStatusOuterGet get)
    {
        if (get == null) {
            throw new ArgumentNullException("get");
        }
        // TODO: 检查get的method中不是BasicGetBgResult
        return get;
    }

    @Override
    public SimpleStatusOuterPst pst(SimpleStatusOuterPst pst)
    {
        if (pst == null) {
            throw new ArgumentNullException("pst");
        }
        // TODO: 检查pst的method中不是BasicGetBgResult
        return pst;
    }

    @Override
    public SimpleStatusOuterDel del(SimpleStatusOuterDel del)
    {
        if (del == null) {
            throw new ArgumentNullException("del");
        }
        // TODO: 检查del的method中不是BasicDelBgResult
        return del;
    }
}

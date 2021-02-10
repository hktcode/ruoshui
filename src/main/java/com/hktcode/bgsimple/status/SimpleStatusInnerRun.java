/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleStatusInnerRun extends SimpleStatusInner
{
    public static SimpleStatusInnerRun of(ImmutableList<SimpleMethodAllResult<?>> result)
    {
        if (result == null) {
            throw new ArgumentNullException("result");
        }
        return new SimpleStatusInnerRun(result);
    }

    private SimpleStatusInnerRun(ImmutableList<SimpleMethodAllResult<?>> result)
    {
        super(result);
    }

    @Override
    public SimpleStatusOuter outer(SimpleStatusOuter outer)
    {
        if (outer == null) {
            throw new ArgumentNullException("outer");
        }
        // TODO: 检查get的method中不是BasicGetBgResult
        return outer;
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.*;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.Phaser;

public class SimpleStatusInnerEnd extends SimpleStatusInner
{
    public static SimpleStatusInnerEnd of(ImmutableList<SimpleMethodAllResultEnd<?>> result)
    {
        if (result == null) {
            throw new ArgumentNullException("result");
        }
        return new SimpleStatusInnerEnd(result);
    }

    private SimpleStatusInnerEnd(ImmutableList<SimpleMethodAllResultEnd<?>> result)
    {
        super(result);
    }

    @Override
    public SimpleStatusOuter outer(SimpleStatusOuter outer)
    {
        if (outer == null) {
            throw new ArgumentNullException("outer");
        }
        // TODO: 检查pst中的Phaser是否已经终止.
        SimpleMethod<?>[] method = new SimpleMethod[result.size()];
        result.toArray(method);
        return SimpleStatusOuter.of(new Phaser(1), method);
    }
}

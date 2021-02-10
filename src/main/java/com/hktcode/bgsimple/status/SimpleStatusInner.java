/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.future.SimpleFutureInner;
import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.lang.exception.ArgumentNullException;

public abstract class SimpleStatusInner implements SimpleStatus
{
    public final ImmutableList<? extends SimpleMethodAllResult<?>> result;

    protected <R extends SimpleMethodAllResult<?>> SimpleStatusInner(ImmutableList<R> result)
    {
        this.result = result;
    }

    public SimpleFutureInner newFuture(SimpleHolder status)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return SimpleFutureInner.of(this);
    }
}

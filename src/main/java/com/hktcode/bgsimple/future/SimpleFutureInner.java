/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.SimpleMethodResult;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.lang.exception.ArgumentNullException;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class SimpleFutureInner extends SimpleFuture
{
    public static SimpleFutureInner of(SimpleStatusInner origin)
    {
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new SimpleFutureInner(origin);
    }

    protected SimpleFutureInner(SimpleStatusInner origin)
    {
        super(origin);
    }

    @Override
    public boolean isDone()
    {
        return true;
    }

    @Override
    public ImmutableList<? extends SimpleMethodResult> get()
    {
        return ((SimpleStatusInner)this.origin).result;
    }

    @Override
    public ImmutableList<? extends SimpleMethodResult> get(long timeout, @Nonnull TimeUnit unit) //
    {
        return ((SimpleStatusInner)this.origin).result;
    }
}

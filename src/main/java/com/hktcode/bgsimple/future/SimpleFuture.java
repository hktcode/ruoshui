/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.bgsimple.status.SimpleStatus;

import javax.annotation.Nonnull;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class SimpleFuture
    implements Future<ImmutableList<? extends SimpleMethodAllResult<?>>>
{
    protected final SimpleStatus origin;

    protected SimpleFuture(SimpleStatus origin)
    {
        this.origin = origin;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return false;
    }

    @Override
    public boolean isCancelled()
    {
        return false;
    }

    @Override
    public abstract ImmutableList<? extends SimpleMethodAllResult<?>> get() throws InterruptedException;

    @Override
    public abstract ImmutableList<? extends SimpleMethodAllResult<?>> //
    get(long timeout, @Nonnull TimeUnit unit) //
        throws InterruptedException, TimeoutException;
}

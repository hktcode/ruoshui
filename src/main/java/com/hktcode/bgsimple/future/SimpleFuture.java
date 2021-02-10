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
import java.util.concurrent.atomic.AtomicReference;

public abstract class SimpleFuture<S extends SimpleStatus>
    implements Future<ImmutableList<SimpleMethodAllResult<?>>>
{
    protected final AtomicReference<SimpleStatus> status;

    protected final S origin;

    protected SimpleFuture(AtomicReference<SimpleStatus> status, S origin)
    {
        this.status = status;
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
    public abstract ImmutableList<SimpleMethodAllResult<?>> get() throws InterruptedException;

    @Override
    public abstract ImmutableList<SimpleMethodAllResult<?>> //
    get(long timeout, @Nonnull TimeUnit unit) //
        throws InterruptedException, TimeoutException;
}

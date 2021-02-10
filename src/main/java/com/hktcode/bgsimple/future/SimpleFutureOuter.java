/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.bgsimple.status.SimpleStatusOuter;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimpleFutureOuter extends SimpleFuture
{
    private final SimpleHolder status;

    public static SimpleFutureOuter of(SimpleHolder status, SimpleStatusOuter origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new SimpleFutureOuter(status, origin);
    }

    protected SimpleFutureOuter(SimpleHolder status, SimpleStatusOuter origin)
    {
        super(origin);
        this.status = status;
    }

    @Override
    public boolean isDone()
    {
        return ((SimpleStatusOuter)origin).phaser.isTerminated();
    }

    @Override
    public ImmutableList<? extends SimpleMethodAllResult<?>> get() throws InterruptedException
    {
        SimpleFuture result = this.status.inner();
        return result.get();
    }

    @Override
    public ImmutableList<? extends SimpleMethodAllResult<?>> //
    get(long timeout, @Nonnull TimeUnit unit) //
        throws InterruptedException, TimeoutException
    {
        SimpleFuture result = this.status.inner(timeout, unit);
        return result.get(timeout, unit);
    }
}

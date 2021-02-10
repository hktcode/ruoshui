/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.method.SimpleMethodResult;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.TimeUnit;

public abstract class SimpleStatusInner implements SimpleStatus
{
    public final ImmutableList<? extends SimpleMethodResult> result;

    protected <R extends SimpleMethodResult> SimpleStatusInner(ImmutableList<R> result)
    {
        this.result = result;
    }

    @Override
    public SimpleStatusInner inner()
    {
        return this;
    }
}

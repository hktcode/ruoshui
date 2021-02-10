/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.status;

import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.future.SimpleFuture;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface SimpleStatus
{
    SimpleStatusOuter outer(SimpleStatusOuter outer);
    SimpleStatusInner inner() throws InterruptedException;
    SimpleStatusInner inner(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException;

    SimpleFuture newFuture(SimpleHolder status);
}

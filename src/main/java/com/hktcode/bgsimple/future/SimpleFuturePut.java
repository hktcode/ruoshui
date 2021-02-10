/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterPut;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleFuturePut extends SimpleFuture<SimpleStatusOuterPut>
{
    public static SimpleFuturePut //
    of(AtomicReference<SimpleStatus> status, SimpleStatusOuterPut origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new SimpleFuturePut(status, origin);
    }

    private SimpleFuturePut(AtomicReference<SimpleStatus> status, SimpleStatusOuterPut origin)
    {
        super(status, origin);
    }
}

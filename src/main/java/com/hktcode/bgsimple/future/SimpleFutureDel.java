/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterDel;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleFutureDel extends SimpleFuture<SimpleStatusOuterDel>
{
    public static SimpleFutureDel //
    of(AtomicReference<SimpleStatus> status, SimpleStatusOuterDel origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new SimpleFutureDel(status, origin);
    }

    private SimpleFutureDel(AtomicReference<SimpleStatus> status, SimpleStatusOuterDel origin)
    {
        super(status, origin);
    }
}

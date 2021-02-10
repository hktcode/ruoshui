/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterPst;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleFuturePst extends SimpleFutureOuter<SimpleStatusOuterPst>
{
    public static SimpleFuturePst //
    of(AtomicReference<SimpleStatus> status, SimpleStatusOuterPst origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new SimpleFuturePst(status, origin);
    }

    private SimpleFuturePst(AtomicReference<SimpleStatus> status, SimpleStatusOuterPst origin)
    {
        super(status, origin);
    }
}

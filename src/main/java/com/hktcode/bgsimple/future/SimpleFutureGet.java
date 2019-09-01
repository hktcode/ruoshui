/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterGet;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleFutureGet extends SimpleFuture<SimpleStatusOuterGet>
{
    public static SimpleFutureGet //
    of(AtomicReference<SimpleStatus> status, SimpleStatusOuterGet origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new SimpleFutureGet(status, origin);
    }

    private SimpleFutureGet(AtomicReference<SimpleStatus> status, SimpleStatusOuterGet origin)
    {
        super(status, origin);
    }

    protected SimpleMethodAllResult[] getResult()
    {
        SimpleMethodAllResult[] result = new SimpleMethodAllResult[origin.method.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (SimpleMethodAllResult)this.origin.method[i];
        }
        return result;
    }
}

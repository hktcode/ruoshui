/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterPst;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleFuturePst extends SimpleFuture<SimpleStatusOuterPst>
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

    protected SimpleMethodAllResult[] getResult()
    {
        SimpleMethodAllResult[] result = new SimpleMethodAllResult[origin.method.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (SimpleMethodAllResult)this.origin.method[i];
        }
        return result;
    }
}

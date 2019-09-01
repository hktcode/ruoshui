/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;

import javax.annotation.Nonnull;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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

    protected SimpleMethodAllResult[] getResult()
    {
        SimpleMethodAllResult[] result = new SimpleMethodAllResult[origin.method.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (SimpleMethodAllResult)this.origin.method[i];
        }
        return result;
    }
}

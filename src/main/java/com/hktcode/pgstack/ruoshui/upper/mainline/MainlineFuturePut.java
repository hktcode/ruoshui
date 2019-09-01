/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterGet;
import com.hktcode.bgsimple.status.SimpleStatusOuterPut;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class MainlineFuturePut extends MainlineFuture<SimpleStatusOuterPut>
{
    public static //
    MainlineFuturePut of(AtomicReference<SimpleStatus> status, SimpleStatusOuterPut origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new MainlineFuturePut(status, origin);
    }

    private MainlineFuturePut //
        (AtomicReference<SimpleStatus> status, SimpleStatusOuterPut origin)
    {
        super(status, origin);
    }

    @Override
    protected MainlineResult getResult()
    {
        return super.getResult(origin.method[0]);
    }
}

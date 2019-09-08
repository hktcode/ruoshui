/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterPut;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SnapshotFuturePut extends SnapshotFuture<SimpleStatusOuterPut>
{
    public static //
    SnapshotFuturePut of(AtomicReference<SimpleStatus> status, SimpleStatusOuterPut origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new SnapshotFuturePut(status, origin);
    }

    private SnapshotFuturePut //
        (AtomicReference<SimpleStatus> status, SimpleStatusOuterPut origin)
    {
        super(status, origin);
    }

    @Override
    protected SnapshotResult getResult()
    {
        return super.getResult(origin.method[0]);
    }
}

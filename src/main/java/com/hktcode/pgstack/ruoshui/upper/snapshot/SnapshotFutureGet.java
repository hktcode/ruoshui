/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterGet;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SnapshotFutureGet extends SnapshotFuture<SimpleStatusOuterGet>
{
    public static //
    SnapshotFuture of(AtomicReference<SimpleStatus> status, SimpleStatusOuterGet origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new SnapshotFutureGet(status, origin);
    }

    private SnapshotFutureGet //
        (AtomicReference<SimpleStatus> status, SimpleStatusOuterGet origin)
    {
        super(status, origin);
    }

    @Override
    protected SnapshotResult getResult()
    {
        return super.getResult(origin.method[0]);
    }
}

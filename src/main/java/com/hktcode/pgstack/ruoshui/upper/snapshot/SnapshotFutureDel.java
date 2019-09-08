/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterDel;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SnapshotFutureDel extends SnapshotFuture<SimpleStatusOuterDel>
{
    public static //
    SnapshotFutureDel of(AtomicReference<SimpleStatus> status, SimpleStatusOuterDel origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new SnapshotFutureDel(status, origin);
    }

    private SnapshotFutureDel //
        (AtomicReference<SimpleStatus> status, SimpleStatusOuterDel origin)
    {
        super(status, origin);
    }

    @Override
    protected SnapshotResult getResult()
    {
        return super.getResult(origin.method[0]);
    }
}

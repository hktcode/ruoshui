/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusOuterPst;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SnapshotFuturePst extends SnapshotFuture<SimpleStatusOuterPst>
{
    public static //
    SnapshotFuturePst of(AtomicReference<SimpleStatus> status, SimpleStatusOuterPst origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new SnapshotFuturePst(status, origin);
    }

    private SnapshotFuturePst //
        (AtomicReference<SimpleStatus> status, SimpleStatusOuterPst origin)
    {
        super(status, origin);
    }

    @Override
    protected SnapshotResult getResult()
    {
        return super.getResult(origin.method[0]);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.status.*;

import javax.annotation.Nonnull;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SnapshotFuture<S extends SimpleStatusOuter> //
    implements Future<SnapshotResult>
{
    protected final AtomicReference<SimpleStatus> status;

    protected final S origin;

    SnapshotFuture(AtomicReference<SimpleStatus> status, S origin)
    {
        this.status = status;
        this.origin = origin;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return false;
    }

    @Override
    public boolean isCancelled()
    {
        return false;
    }

    @Override
    public boolean isDone()
    {
        return origin.phaser.isTerminated();
    }

    @Override
    public SnapshotResult get() throws InterruptedException
    {
        Phaser phaser = origin.phaser;
        phaser.awaitAdvanceInterruptibly(phaser.arrive());
        return this.getResult();
    }

    @Override
    public SnapshotResult get(long timeout, @Nonnull TimeUnit unit) //
        throws InterruptedException, TimeoutException
    {
        Phaser phaser = origin.phaser;
        phaser.awaitAdvanceInterruptibly(phaser.arrive(), timeout, unit);
        return this.getResult();
    }

    protected abstract SnapshotResult getResult();

    <W> SnapshotResult getResult(W method)
    {
        if (method instanceof SnapshotResultEnd) {
            SnapshotResultEnd r = (SnapshotResultEnd)method;
            SimpleStatusInnerEnd end = SimpleStatusInnerEnd.of(ImmutableList.of(r));
            if (!this.status.compareAndSet(origin, end)) {
                // TODO:
                throw new RuntimeException("concurrent error");
            }
        }
        origin.phaser.arriveAndDeregister();
        SnapshotResult result = (SnapshotResult)method;
        SimpleStatusInner future;
        if (result instanceof SnapshotResultEnd) {
            future = SimpleStatusInnerEnd.of(
                ImmutableList.of((SnapshotResultEnd)result)
            );
        }
        else {
            future = SimpleStatusInnerRun.of();
        }
        this.status.compareAndSet(origin, future);
        return result;
    }
}

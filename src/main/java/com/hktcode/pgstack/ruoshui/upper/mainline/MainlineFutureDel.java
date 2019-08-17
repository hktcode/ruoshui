/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerEnd;
import com.hktcode.bgsimple.status.SimpleStatusOuter;
import com.hktcode.bgsimple.status.SimpleStatusOuterDel;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineFutureDel implements Future<MainlineResult>
{
    public static MainlineFutureDel of //
        (AtomicReference<SimpleStatus> status, SimpleStatusOuterDel origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new MainlineFutureDel(status, origin);
    }

    private final AtomicReference<SimpleStatus> status;

    private final SimpleStatusOuterDel origin;

    private MainlineFutureDel(AtomicReference<SimpleStatus> status, SimpleStatusOuterDel origin)
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
    public MainlineResult get() throws InterruptedException, ExecutionException
    {
        Phaser phaser = origin.phaser;
        phaser.awaitAdvanceInterruptibly(phaser.arrive());
        if (origin.method[0] instanceof MainlineResultEnd) {
            SimpleStatusInnerEnd end = SimpleStatusInnerEnd.of(
                // ImmutableList.<SimpleMethodAllResultEnd>copyOf(origin.method)
                ImmutableList.<SimpleMethodAllResultEnd>of()
            );
            if (!this.status.compareAndSet(origin, end)) {
                throw new RuntimeException("concurrent error");
            }
        }
        origin.phaser.arriveAndDeregister();
        return (MainlineResult)origin.method[0];
    }

    @Override
    public MainlineResult get(long timeout, TimeUnit unit) //
        throws InterruptedException, ExecutionException, TimeoutException
    {
        Phaser phaser = origin.phaser;
        phaser.awaitAdvanceInterruptibly(phaser.arrive(), timeout, unit);
        if (origin.method[0] instanceof MainlineResultEnd) {
            SimpleStatusInnerEnd end = SimpleStatusInnerEnd.of(
                // ImmutableList.<SimpleMethodAllResultEnd>copyOf(origin.method)
                ImmutableList.<SimpleMethodAllResultEnd>of()
            );
            if (!this.status.compareAndSet(origin, end)) {
                throw new RuntimeException("concurrent error");
            }
        }
        origin.phaser.arriveAndDeregister();
        return (MainlineResult)origin.method[0];
    }
}

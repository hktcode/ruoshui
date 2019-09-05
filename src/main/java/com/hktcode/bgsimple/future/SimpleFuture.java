/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.*;

import javax.annotation.Nonnull;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public abstract class SimpleFuture<S extends SimpleStatusOuter>
    implements Future<ImmutableList<SimpleMethodAllResult>>
{
    protected final AtomicReference<SimpleStatus> status;

    protected final S origin;

    protected SimpleFuture(AtomicReference<SimpleStatus> status, S origin)
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
    public ImmutableList<SimpleMethodAllResult> get() throws InterruptedException
    {
        Phaser phaser = origin.phaser;
        phaser.awaitAdvanceInterruptibly(phaser.arrive());
        SimpleMethodAllResult[] result = this.getResult();
        SimpleStatus future = this.newStatus(result);
        if (!(future instanceof SimpleStatusOuterDel)) {
            this.status.compareAndSet(origin, future);
            origin.phaser.arriveAndDeregister();
            return ImmutableList.copyOf(result);
        }
        else if (future == origin) {
            // TODO 不可能发生.
            throw new RuntimeException("should never happen");
        }
        else if (this.status.compareAndSet(origin, future)) {
            origin.phaser.arriveAndDeregister();
            SimpleStatusOuterDel f = (SimpleStatusOuterDel)future;
            SimpleFutureDel del = SimpleFutureDel.of(this.status, f);
            return del.get();
        }
        else {
            origin.phaser.arriveAndDeregister();
            return ImmutableList.copyOf(result);
        }
    }

    @Override
    public ImmutableList<SimpleMethodAllResult> //
    get(long timeout, @Nonnull TimeUnit unit) //
        throws InterruptedException, TimeoutException
    {
        Phaser phaser = origin.phaser;
        phaser.awaitAdvanceInterruptibly(phaser.arrive(), timeout, unit);
        SimpleMethodAllResult[] result = this.getResult();
        SimpleStatus future = this.newStatus(result);
        if (!(future instanceof SimpleStatusOuterDel)) {
            this.status.compareAndSet(origin, future);
            origin.phaser.arriveAndDeregister();
            return ImmutableList.copyOf(result);
        }
        else if (this.status.compareAndSet(origin, future)) {
            origin.phaser.arriveAndDeregister();
            SimpleStatusOuterDel f = (SimpleStatusOuterDel)future;
            SimpleFutureDel del = SimpleFutureDel.of(this.status, f);
            return del.get(timeout, unit);
        }
        else {
            origin.phaser.arriveAndDeregister();
            return ImmutableList.copyOf(result);
        }
    }

    protected abstract SimpleMethodAllResult[] getResult();

    private SimpleStatus newStatus(SimpleMethodAllResult[] result)
    {
        int delCount = 0;
        SimpleMethodDel[] method = new SimpleMethodDel[result.length];
        SimpleMethodAllResultEnd[] endresult = new SimpleMethodAllResultEnd[result.length];
        for (int i = 0; i < result.length; ++i) {
            if (result[i] instanceof SimpleMethodAllResultEnd) {
                method[i] = result[i];
                endresult[i] = (SimpleMethodAllResultEnd)result[i];
            } else {
                method[i] = SimpleMethodDelParamsDefault.of();
                ++delCount;
            }
        }
        if (delCount == 0) {
            return SimpleStatusInnerEnd.of(ImmutableList.copyOf(endresult));
        } else if (delCount == result.length) {
            return SimpleStatusInnerRun.of();
        } else if (this.origin instanceof SimpleStatusOuterDel) {
            return this.origin;
        } else {
            return SimpleStatusOuterDel.of(method, new Phaser(delCount + 1));
        }
    }
}

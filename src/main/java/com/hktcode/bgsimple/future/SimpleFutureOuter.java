/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.SimpleMethodAllResult;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.method.SimpleMethodDel;
import com.hktcode.bgsimple.method.SimpleMethodDelParamsDefault;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class SimpleFutureOuter<S extends SimpleStatusOuter> extends SimpleFuture<S>
{
    public static <S extends SimpleStatusOuter> SimpleFutureOuter<S> //
    of(AtomicReference<SimpleStatus> status, S origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new SimpleFutureOuter<>(status, origin);
    }

    protected SimpleFutureOuter(AtomicReference<SimpleStatus> status, S origin)
    {
        super(status, origin);
    }

    @Override
    public boolean isDone()
    {
        return origin.phaser.isTerminated();
    }

    @Override
    public ImmutableList<SimpleMethodAllResult<?>> get() throws InterruptedException
    {
        Phaser phaser = origin.phaser;
        phaser.awaitAdvanceInterruptibly(phaser.arrive());
        SimpleStatus future = this.newStatus();
        boolean cas = this.status.compareAndSet(origin, future);
        phaser.arriveAndDeregister();
        if (future instanceof SimpleStatusOuterDel && cas) {
            SimpleStatusOuterDel f = (SimpleStatusOuterDel)future;
            SimpleFutureDel del = SimpleFutureDel.of(this.status, f);
            return del.get();
        }
        return ImmutableList.copyOf((SimpleMethodAllResultEnd<?>[])this.origin.method);
    }

    @Override
    public ImmutableList<SimpleMethodAllResult<?>> //
    get(long timeout, @Nonnull TimeUnit unit) //
        throws InterruptedException, TimeoutException
    {
        Phaser phaser = origin.phaser;
        phaser.awaitAdvanceInterruptibly(phaser.arrive(), timeout, unit);
        SimpleStatus future = this.newStatus();
        boolean cas = this.status.compareAndSet(origin, future);
        phaser.arriveAndDeregister();
        if (future instanceof SimpleStatusOuterDel && cas) {
            SimpleStatusOuterDel f = (SimpleStatusOuterDel)future;
            SimpleFutureDel del = SimpleFutureDel.of(this.status, f);
            return del.get(timeout, unit);
        }
        return ImmutableList.copyOf((SimpleMethodAllResultEnd<?>[])this.origin.method);
    }

    private SimpleStatus newStatus()
    {
        int delCount = 0;
        final int methodlength = this.origin.method.length;
        SimpleMethodDel<?>[] method = new SimpleMethodDel[methodlength];
        for (int i = 0; i < methodlength; ++i) {
            if (this.origin.method[i] instanceof SimpleMethodAllResultEnd) {
                method[i] = (SimpleMethodAllResultEnd<?>)this.origin.method[i];
            } else {
                method[i] = SimpleMethodDelParamsDefault.of();
                ++delCount;
            }
        }
        if (delCount == 0) {
            return SimpleStatusInnerEnd.of(ImmutableList.copyOf((SimpleMethodAllResultEnd<?>[])this.origin.method));
        } else if (delCount == methodlength) {
            return SimpleStatusInnerRun.of();
        } else if (this.origin instanceof SimpleStatusOuterDel) {
            return this.origin;
        } else {
            logger.info("new status outer del: delCount={}", delCount);
            return SimpleStatusOuterDel.of(method, new Phaser(methodlength + 1));
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SimpleFutureOuter.class);
}

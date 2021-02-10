/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.future;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.*;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.lang.exception.NeverHappenAssertionError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimpleFutureOuter extends SimpleFuture
{
    private final SimpleHolder status;

    public static SimpleFutureOuter of(SimpleHolder status, SimpleStatusOuter origin)
    {
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        if (origin == null) {
            throw new ArgumentNullException("origin");
        }
        return new SimpleFutureOuter(status, origin);
    }

    protected SimpleFutureOuter(SimpleHolder status, SimpleStatusOuter origin)
    {
        super(origin);
        this.status = status;
    }

    @Override
    public boolean isDone()
    {
        return ((SimpleStatusOuter)origin).phaser.isTerminated();
    }

    @Override
    public ImmutableList<? extends SimpleMethodAllResult<?>> get() throws InterruptedException
    {
        Phaser phaser = ((SimpleStatusOuter)origin).phaser;
        phaser.awaitAdvanceInterruptibly(phaser.arrive());
        SimpleStatus future = this.newStatus();
        this.status.cas(origin, future);
        phaser.arriveAndDeregister();
        SimpleFuture result = future.newFuture(this.status);
        return result.get();
    }

    @Override
    public ImmutableList<? extends SimpleMethodAllResult<?>> //
    get(long timeout, @Nonnull TimeUnit unit) //
        throws InterruptedException, TimeoutException
    {
        Phaser phaser = ((SimpleStatusOuter)origin).phaser;
        phaser.awaitAdvanceInterruptibly(phaser.arrive(), timeout, unit);
        SimpleStatus future = this.newStatus();
        this.status.cas(origin, future);
        phaser.arriveAndDeregister();
        SimpleFuture result = future.newFuture(this.status);
        return result.get(timeout, unit);
    }

    private SimpleStatus newStatus()
    {
        int delCount = 0;
        SimpleMethod<?>[] originmethod = ((SimpleStatusOuter)this.origin).method;
        final int methodlength = originmethod.length;
        SimpleMethodDel<?>[] method = new SimpleMethodDel[methodlength];
        for (int i = 0; i < methodlength; ++i) {
            if (originmethod[i] instanceof SimpleMethodAllResultEnd) {
                method[i] = (SimpleMethodAllResult<?>)originmethod[i];
            } else if (originmethod[i] instanceof SimpleMethodAllResultRun) {
                method[i] = SimpleMethodDelParamsDefault.of();
                ++delCount;
            }
            else {
                throw new NeverHappenAssertionError();
            }
        }
        if (delCount == 0) {
            return SimpleStatusInnerEnd.of(ImmutableList.copyOf((SimpleMethodAllResultEnd<?>[])originmethod));
        } else if (delCount == methodlength) {
            return SimpleStatusInnerRun.of(ImmutableList.copyOf((SimpleMethodAllResultRun<?>[])originmethod));
        } else if (this.origin instanceof SimpleStatusOuterDel) {
            return this.origin;
        } else {
            logger.info("new status outer del: delCount={}", delCount);
            return SimpleStatusOuterDel.of(method, new Phaser(methodlength + 1));
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SimpleFutureOuter.class);
}

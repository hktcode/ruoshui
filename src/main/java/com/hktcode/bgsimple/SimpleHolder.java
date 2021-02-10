/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.SimpleMethodResult;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusEnd;
import com.hktcode.bgsimple.status.SimpleStatusCmd;
import com.hktcode.bgsimple.status.SimpleStatusRun;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleHolder
{
    public static SimpleHolder of()
    {
        return new SimpleHolder();
    }

    private final AtomicReference<SimpleStatus> status;

    private SimpleHolder()
    {
        this.status = new AtomicReference<>(SimpleStatusRun.of());
    }

    public SimpleStatus run(BgWorker wkstep, int number) throws InterruptedException
    {
        SimpleStatus origin;
        while ((origin = this.status.get()) instanceof SimpleStatusCmd) {
            SimpleStatus future = origin.run(wkstep, number);
            this.status.compareAndSet(origin, future);
        }
        return origin;
    }

    public SimpleStatusEnd end(BgWorker wkstep, int number) throws InterruptedException
    {
        SimpleStatus origin;
        while (!((origin = this.status.get()) instanceof SimpleStatusEnd)) {
            SimpleStatus future = origin.run(wkstep, number);
            this.status.compareAndSet(origin, future);
        }
        return (SimpleStatusEnd) origin;
    }

    public ImmutableList<SimpleMethodResult> cmd(SimpleStatusCmd cmd)
            throws InterruptedException
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        // TODO: 判断get中的bgMethod不是bgResult.
        SimpleStatus origin;
        SimpleStatusCmd future;
        do {
            origin = this.status.get();
            future = origin.cmd(cmd);
        } while (/*  */future == origin  //
                /**/|| (/**/future == cmd  //
                /*     */&& !this.status.compareAndSet(origin, future) //
                /*   */)
            /**/);
        return future.inner();
    }
}

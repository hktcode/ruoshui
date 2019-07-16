/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.bgsimple.status.SimpleStatusOuter;

import java.util.concurrent.atomic.AtomicReference;

public abstract class SimpleWorker<W extends SimpleWorker<W>> //
    implements BgWorker<W>
{
    public final int number;

    protected final AtomicReference<SimpleStatus> status;

    protected SimpleWorker(AtomicReference<SimpleStatus> status, int number)
    {
        this.number = number;
        this.status = status;
    }

    @Override
    public SimpleStatusInner newStatus(W wkstep) throws InterruptedException
    {
        SimpleStatus origin;
        while (!((origin = this.status.get()) instanceof SimpleStatusInner)) {
            SimpleStatusOuter outer = (SimpleStatusOuter) origin;
            outer.newStatus(wkstep);
        }
        return (SimpleStatusInner) origin;
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import com.hktcode.bgmethod.*;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.bgsimple.status.SimpleStatusOuter;

import java.util.concurrent.atomic.AtomicReference;

public abstract class SimpleWorker<W extends SimpleWorker<W, M>, M> //
    implements BgWorker<W, M>
{
    public final int number;

    protected final AtomicReference<SimpleStatus<W, M>> status;

    protected SimpleWorker(AtomicReference<SimpleStatus<W, M>> status, int number)
    {
        this.number = number;
        this.status = status;
    }

    public SimpleStatusInner<W, M> newStatus(W myown, M metric) //
        throws InterruptedException
    {
        SimpleStatus<W, M> origin;
        while (!((origin = this.status.get()) instanceof SimpleStatusInner)) {
            SimpleStatusOuter<?, W, M> outer = (SimpleStatusOuter<?, W, M>) origin;
            outer.newStatus(myown, metric);
        }
        return (SimpleStatusInner<W, M>) origin;
    }
}

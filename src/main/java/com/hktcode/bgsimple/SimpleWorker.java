/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.bgsimple.status.SimpleStatusOuter;

public abstract class SimpleWorker<W extends BgWorker<W>>
{
    public final int number;

    public final SimpleHolder status;

    protected SimpleWorker(SimpleHolder status, int number)
    {
        this.number = number;
        this.status = status;
    }

    public SimpleStatusInner newStatus(W wkstep) throws InterruptedException
    {
        SimpleStatus origin;
        while (!((origin = this.status.get()) instanceof SimpleStatusInner)) {
            SimpleStatusOuter outer = (SimpleStatusOuter) origin;
            outer.newStatus(wkstep, number);
        }
        return (SimpleStatusInner) origin;
    }
}

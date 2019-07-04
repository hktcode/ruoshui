/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import com.hktcode.bgmethod.BgWorker;
import com.hktcode.bgsimple.status.SimpleStatus;

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
}

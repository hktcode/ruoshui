/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.bgsimple.status.SimpleStatusOuter;

public abstract class SimpleWorker
{
    public final int number;

    public final SimpleHolder status;

    protected SimpleWorker(SimpleHolder status, int number)
    {
        this.number = number;
        this.status = status;
    }
}

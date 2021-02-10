/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

public abstract class SimpleWorker implements BgWorker
{
    public final int number;

    public final SimpleHolder status;

    protected SimpleWorker(SimpleHolder status, int number)
    {
        this.number = number;
        this.status = status;
    }
}

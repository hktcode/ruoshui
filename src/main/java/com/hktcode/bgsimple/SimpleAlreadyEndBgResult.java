/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public abstract class SimpleAlreadyEndBgResult<T extends SimpleBasicBgWorker<T>> //
    implements SimpleBasicEndBgResult<T> //
{
    public final ZonedDateTime endtime;

    protected SimpleAlreadyEndBgResult(ZonedDateTime endtime)
    {
        this.endtime = endtime;
    }

    @Override
    public SimpleAlreadyEndBgResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return this;
    }
}

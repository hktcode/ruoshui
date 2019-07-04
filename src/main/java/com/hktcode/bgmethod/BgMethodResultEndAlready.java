/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public abstract class BgMethodResultEndAlready<T extends SimpleBasicBgWorker<T>> //
    implements BgMethodResultEnd<T> //
{
    public final ZonedDateTime endtime;

    protected BgMethodResultEndAlready(ZonedDateTime endtime)
    {
        this.endtime = endtime;
    }

    @Override
    public BgMethodResultEndAlready<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return this;
    }
}

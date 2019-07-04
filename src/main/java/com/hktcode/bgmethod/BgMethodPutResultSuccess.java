/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class BgMethodPutResultSuccess<C, T extends SimpleBgWorker<T>> implements BgMethodPutResult<T>
{
    public static <C, T extends SimpleBgWorker<T>> BgMethodPutResultSuccess<C, T> of (ZonedDateTime startat, C configs)
    {
        if (startat == null) {
            throw new ArgumentNullException("startat");
        }
        if (configs == null) {
            throw new ArgumentNullException("config");
        }
        return new BgMethodPutResultSuccess<>(startat, configs);
    }

    public final ZonedDateTime startat;

    public final C configs;

    private BgMethodPutResultSuccess(ZonedDateTime startat, C configs)
    {
        this.startat = startat;
        this.configs = configs;
    }

    @Override
    public BgMethodPutResultSuccess<C, T> run(T worker, Throwable reasons, ZonedDateTime endtime)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return this;
    }
}
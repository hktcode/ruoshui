/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class BgMethodResultEndSuccess<C, M, T extends SimpleBasicBgWorker<T>> extends BgMethodResultEndReality<C, M, T>
{
    public static<C, M, T extends SimpleBasicBgWorker<T>> BgMethodResultEndSuccess<C, M, T> of(C configs, M metrics, ZonedDateTime endtime)
    {
        if (configs == null) {
            throw new ArgumentNullException("config");
        }
        if (metrics == null) {
            throw new ArgumentNullException("metric");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return new BgMethodResultEndSuccess<>(configs, metrics, endtime);
    }

    private BgMethodResultEndSuccess(C configs, M metrics, ZonedDateTime endtime)
    {
        super(configs, metrics, endtime);
    }
}

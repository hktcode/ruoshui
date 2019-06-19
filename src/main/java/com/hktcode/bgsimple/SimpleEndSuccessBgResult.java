/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgsimple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class SimpleEndSuccessBgResult<C, M, T extends SimpleBasicBgWorker<T>> extends SimpleRealityEndBgResult<C, M, T>
{
    public static<C, M, T extends SimpleBasicBgWorker<T>> SimpleEndSuccessBgResult<C, M, T> of(C configs, M metrics, ZonedDateTime endtime)
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
        return new SimpleEndSuccessBgResult<>(configs, metrics, endtime);
    }

    private SimpleEndSuccessBgResult(C configs, M metrics, ZonedDateTime endtime)
    {
        super(configs, metrics, endtime);
    }
}

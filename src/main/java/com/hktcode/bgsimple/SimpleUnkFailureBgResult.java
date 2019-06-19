/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgsimple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class SimpleUnkFailureBgResult<C, M, T extends SimpleBasicBgWorker<T>> //
    extends SimpleRealityEndBgResult<C, M, T>
{
    public static <C, M, T extends SimpleBasicBgWorker<T>> //
    SimpleUnkFailureBgResult<C, M, T> of(Throwable reasons, C configs, M metrics, ZonedDateTime endtime)
    {
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (configs == null) {
            throw new ArgumentNullException("config");
        }
        if (metrics == null) {
            throw new ArgumentNullException("metric");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return new SimpleUnkFailureBgResult<>(reasons, configs, metrics, endtime);
    }

    public final Throwable reasons;

    private SimpleUnkFailureBgResult(Throwable reasons, C configs, M metrics, ZonedDateTime endtime)
    {
        super(configs, metrics, endtime);
        this.reasons = reasons;
    }
}

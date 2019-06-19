/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgsimple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class SimpleMiscarriedBgResult<T extends SimpleBasicBgWorker<T>> //
    extends SimpleAlreadyEndBgResult<T> implements SimpleBasicPutBgResult<T>
{
    public static <T extends SimpleBasicBgWorker<T>> SimpleMiscarriedBgResult<T> of (ZonedDateTime endtime)
    {
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return new SimpleMiscarriedBgResult<>(endtime);
    }

    private SimpleMiscarriedBgResult(ZonedDateTime endtime)
    {
        super(endtime);
    }

    @Override
    public SimpleMiscarriedBgResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return this;
    }

    @Override
    public SimpleMiscarriedBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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
        // TODO: logger this should not be happened
        return this;
    }
}

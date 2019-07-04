/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class BgMethodResultMiscarried<T extends SimpleBasicBgWorker<T>> //
    extends BgMethodResultEndAlready<T> implements BgMethodPutResult<T>
{
    public static <T extends SimpleBasicBgWorker<T>> BgMethodResultMiscarried<T> of (ZonedDateTime endtime)
    {
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return new BgMethodResultMiscarried<>(endtime);
    }

    private BgMethodResultMiscarried(ZonedDateTime endtime)
    {
        super(endtime);
    }

    @Override
    public BgMethodResultMiscarried<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return this;
    }

    @Override
    public BgMethodResultMiscarried<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public interface BgMethodDelResult<T extends SimpleBgWorker<T>> //
    extends BgMethodDel<T>, BgMethodResult<T>
{
    @Override
    default BgMethodDelResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        // TODO: logger this should not be happened
        return this;
    }

    @Override
    default BgMethodDelResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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

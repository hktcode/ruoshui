/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public interface SimpleBasicPstBgResult<T extends SimpleBasicBgWorker<T>> //
    extends SimpleBasicPstBgMethod<T>, SimpleBasicBgResult<T>
{
    @Override
    default SimpleBasicPstBgResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return this;
    }

    @Override
    default SimpleBasicPstBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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
        // TODO: logger
        return this;
    }
}

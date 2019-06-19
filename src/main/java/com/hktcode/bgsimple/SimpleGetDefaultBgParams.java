/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgsimple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class SimpleGetDefaultBgParams<T extends SimpleBasicBgWorker<T>> //
    implements SimpleBasicGetBgParams<T>
{
    public static <T extends SimpleBasicBgWorker<T>> SimpleGetDefaultBgParams<T> of()
    {
        return new SimpleGetDefaultBgParams<>();
    }

    @Override
    public SimpleBasicGetBgResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.get();
    }

    @Override
    public SimpleBasicGetBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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
        return worker.get(reasons, endtime);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgsimple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class SimpleDelDefaultBgParams<T extends SimpleBasicBgWorker<T>> //
    implements SimpleBasicDelBgParams<T>
{
    public static <T extends SimpleBasicBgWorker<T>> SimpleDelDefaultBgParams<T> of()
    {
        return new SimpleDelDefaultBgParams<>();
    }

    private SimpleDelDefaultBgParams()
    {
    }

    @Override
    public SimpleBasicDelBgResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.del();
    }

    @Override
    public SimpleBasicDelBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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
        return worker.del(reasons, endtime);
    }
}

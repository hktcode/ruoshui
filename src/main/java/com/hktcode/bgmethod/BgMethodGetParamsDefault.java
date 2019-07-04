/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class BgMethodGetParamsDefault<T extends SimpleBasicBgWorker<T>> //
    implements BgMethodGetParams<T>
{
    public static <T extends SimpleBasicBgWorker<T>> BgMethodGetParamsDefault<T> of()
    {
        return new BgMethodGetParamsDefault<>();
    }

    @Override
    public BgMethodGetResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.get();
    }

    @Override
    public BgMethodGetResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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

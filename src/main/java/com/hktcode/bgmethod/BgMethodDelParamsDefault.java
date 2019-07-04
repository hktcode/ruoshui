/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class BgMethodDelParamsDefault<T extends SimpleBasicBgWorker<T>> //
    implements BgMethodDelParams<T>
{
    public static <T extends SimpleBasicBgWorker<T>> BgMethodDelParamsDefault<T> of()
    {
        return new BgMethodDelParamsDefault<>();
    }

    private BgMethodDelParamsDefault()
    {
    }

    @Override
    public BgMethodDelResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.del();
    }

    @Override
    public BgMethodDelResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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

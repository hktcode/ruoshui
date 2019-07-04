/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class BgMethodPutParamsDefault<T extends SimpleBgWorker<T>> //
    implements BgMethodPutParams<T>
{
    public static <T extends SimpleBgWorker<T>> BgMethodPutParamsDefault<T> of()
    {
        return new BgMethodPutParamsDefault<>();
    }

    private BgMethodPutParamsDefault()
    {
    }

    @Override
    public BgMethodPutResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.put();
    }

    @Override
    public BgMethodPutResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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
        return worker.put(reasons, endtime);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class BgMethodPstParamsDefault<T extends SimpleBasicBgWorker<T>> implements BgMethodPstParams<T>
{
    public static <T extends SimpleBasicBgWorker<T>> BgMethodPstParamsDefault<T> of()
    {
        return new BgMethodPstParamsDefault<>();
    }

    private BgMethodPstParamsDefault()
    {
    }

    @Override
    public BgMethodPstResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.pst();
    }

    @Override
    public BgMethodPstResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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
        return worker.pst(reasons, endtime);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class SimplePutDefaultBgParams<T extends SimpleBasicBgWorker<T>> //
    implements SimpleBasicPutBgParams<T>
{
    public static <T extends SimpleBasicBgWorker<T>> SimplePutDefaultBgParams<T> of()
    {
        return new SimplePutDefaultBgParams<>();
    }

    private SimplePutDefaultBgParams()
    {
    }

    @Override
    public SimpleBasicPutBgResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.put();
    }

    @Override
    public SimpleBasicPutBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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

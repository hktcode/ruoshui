/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class SimplePstDefaultBgParams<T extends SimpleBasicBgWorker<T>> implements SimpleBasicPstBgParams<T>
{
    public static <T extends SimpleBasicBgWorker<T>> SimplePstDefaultBgParams<T> of()
    {
        return new SimplePstDefaultBgParams<>();
    }

    private SimplePstDefaultBgParams()
    {
    }

    @Override
    public SimpleBasicPstBgResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return worker.pst();
    }

    @Override
    public SimpleBasicPstBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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

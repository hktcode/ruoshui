/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class BgMethodParamsPutDefault<T extends SimpleBasicBgWorker<T>> //
    implements BgMethodParamsPut<T>
{
    public static <T extends SimpleBasicBgWorker<T>> BgMethodParamsPutDefault<T> of()
    {
        return new BgMethodParamsPutDefault<>();
    }

    private BgMethodParamsPutDefault()
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

/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgsimple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public interface SimpleBasicEndBgResult<T extends SimpleBasicBgWorker<T>> //
    extends SimpleBasicGetBgResult<T> //
    /*  */, SimpleBasicDelBgResult<T> //
    /*  */, SimpleBasicPstBgResult<T> //
    /*  */, SimpleBasicPutBgResult<T>
{
    @Override
    default SimpleBasicEndBgResult<T> run(T worker)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return this;
    }

    @Override
    default SimpleBasicEndBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime)
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
        return this;
    }
}

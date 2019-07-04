/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public interface SimpleBasicBgWorker<T extends SimpleBasicBgWorker<T>>
{
    default SimpleBasicPstBgResult<T> pst()
    {
        return SimplePstSuccessBgResult.of();
    }

    SimpleBasicPstBgResult<T> pst(Throwable reasons, ZonedDateTime endtime);

    SimpleBasicPutBgResult<T> put();

    default SimpleBasicPutBgResult<T> put(Throwable reasons, ZonedDateTime endtime)
    {
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return SimpleMiscarriedBgResult.of(endtime);
    }

    SimpleBasicGetBgResult<T> get();

    SimpleBasicGetBgResult<T> get(Throwable reasons, ZonedDateTime endtime);

    SimpleBasicDelBgResult<T> del();

    SimpleBasicDelBgResult<T> del(Throwable reasons, ZonedDateTime endtime);
}

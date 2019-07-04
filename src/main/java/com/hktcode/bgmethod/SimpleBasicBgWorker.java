/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public interface SimpleBasicBgWorker<T extends SimpleBasicBgWorker<T>>
{
    default BgMethodPstResult<T> pst()
    {
        return BgMethodPstResultSuccess.of();
    }

    BgMethodPstResult<T> pst(Throwable reasons, ZonedDateTime endtime);

    BgMethodPutResult<T> put();

    default BgMethodPutResult<T> put(Throwable reasons, ZonedDateTime endtime)
    {
        if (reasons == null) {
            throw new ArgumentNullException("reasons");
        }
        if (endtime == null) {
            throw new ArgumentNullException("endtime");
        }
        return BgMethodResultMiscarried.of(endtime);
    }

    BgMethodGetResult<T> get();

    BgMethodGetResult<T> get(Throwable reasons, ZonedDateTime endtime);

    BgMethodDelResult<T> del();

    BgMethodDelResult<T> del(Throwable reasons, ZonedDateTime endtime);
}

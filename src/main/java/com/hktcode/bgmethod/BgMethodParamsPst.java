/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface BgMethodParamsPst<T extends SimpleBasicBgWorker<T>> //
    extends SimpleBasicPstBgMethod<T>, BgMethodParams<T>
{
    @Override
    SimpleBasicPstBgResult<T> run(T worker);

    @Override
    SimpleBasicPstBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

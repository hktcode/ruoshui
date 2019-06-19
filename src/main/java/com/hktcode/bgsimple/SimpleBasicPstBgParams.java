/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgsimple;

import java.time.ZonedDateTime;

public interface SimpleBasicPstBgParams<T extends SimpleBasicBgWorker<T>> //
    extends SimpleBasicPstBgMethod<T>, SimpleBasicBgParams<T>
{
    @Override
    SimpleBasicPstBgResult<T> run(T worker);

    @Override
    SimpleBasicPstBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

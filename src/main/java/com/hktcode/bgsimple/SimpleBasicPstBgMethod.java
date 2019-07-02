/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import java.time.ZonedDateTime;

public interface SimpleBasicPstBgMethod<T extends SimpleBasicBgWorker<T>> extends SimpleBasicBgMethod<T>
{
    @Override
    SimpleBasicPstBgResult<T> run(T worker);

    @Override
    SimpleBasicPstBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

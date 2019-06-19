/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgsimple;

import java.time.ZonedDateTime;

public interface SimpleBasicPutBgMethod<T extends SimpleBasicBgWorker<T>> extends SimpleBasicBgMethod<T>
{
    @Override
    SimpleBasicPutBgResult<T> run(T worker);

    @Override
    SimpleBasicPutBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

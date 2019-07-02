/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import java.time.ZonedDateTime;

public interface SimpleBasicGetBgMethod<T extends SimpleBasicBgWorker<T>> extends SimpleBasicBgMethod<T>
{
    @Override
    SimpleBasicGetBgResult<T> run(T worker);

    @Override
    SimpleBasicGetBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

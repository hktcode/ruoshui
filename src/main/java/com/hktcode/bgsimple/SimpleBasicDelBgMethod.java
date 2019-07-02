/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple;

import java.time.ZonedDateTime;

public interface SimpleBasicDelBgMethod<T extends SimpleBasicBgWorker<T>> //
    extends SimpleBasicBgMethod<T>
{
    @Override
    SimpleBasicDelBgResult<T> run(T worker);

    @Override
    SimpleBasicDelBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

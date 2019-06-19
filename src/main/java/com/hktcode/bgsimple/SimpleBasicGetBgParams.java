/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgsimple;

import java.time.ZonedDateTime;

public interface SimpleBasicGetBgParams<T extends SimpleBasicBgWorker<T>> //
    extends SimpleBasicGetBgMethod<T>, SimpleBasicBgParams<T>
{
    @Override
    SimpleBasicGetBgResult<T> run(T worker);

    @Override
    SimpleBasicGetBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

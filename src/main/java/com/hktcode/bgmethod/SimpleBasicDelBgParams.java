/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface SimpleBasicDelBgParams<T extends SimpleBasicBgWorker<T>> //
    extends SimpleBasicDelBgMethod<T>, SimpleBasicBgParams<T>
{
    @Override
    SimpleBasicDelBgResult<T> run(T worker);

    @Override
    SimpleBasicDelBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface SimpleBasicDelBgMethod<T extends SimpleBasicBgWorker<T>> //
    extends BgMethod<T>
{
    @Override
    SimpleBasicDelBgResult<T> run(T worker);

    @Override
    SimpleBasicDelBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

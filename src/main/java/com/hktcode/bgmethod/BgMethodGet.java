/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface BgMethodGet<T extends SimpleBasicBgWorker<T>> extends BgMethod<T>
{
    @Override
    BgMethodGetResult<T> run(T worker);

    @Override
    BgMethodGetResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

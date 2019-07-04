/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface BgMethodGetParams<T extends SimpleBasicBgWorker<T>> //
    extends BgMethodGet<T>, BgMethodParams<T>
{
    @Override
    BgMethodGetResult<T> run(T worker);

    @Override
    BgMethodGetResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

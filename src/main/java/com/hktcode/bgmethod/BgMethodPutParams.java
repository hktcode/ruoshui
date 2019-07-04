/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface BgMethodPutParams<T extends SimpleBasicBgWorker<T>> //
    extends BgMethodPut<T>, BgMethodParams<T>
{
    @Override
    BgMethodPutResult<T> run(T worker);

    @Override
    BgMethodPutResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

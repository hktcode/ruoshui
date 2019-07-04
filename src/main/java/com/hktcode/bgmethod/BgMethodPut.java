/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface BgMethodPut<T extends SimpleBgWorker<T>> extends BgMethod<T>
{
    @Override
    BgMethodPutResult<T> run(T worker);

    @Override
    BgMethodPutResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

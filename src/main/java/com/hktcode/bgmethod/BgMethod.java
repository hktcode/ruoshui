/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface BgMethod<T extends SimpleBgWorker<T>>
{
    BgMethodResult<T> run(T worker);

    BgMethodResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

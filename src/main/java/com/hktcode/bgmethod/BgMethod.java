/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgmethod;

import java.time.ZonedDateTime;

public interface BgMethod<T extends SimpleBasicBgWorker<T>>
{
    SimpleBasicBgResult<T> run(T worker);

    SimpleBasicBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

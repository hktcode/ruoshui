/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgsimple;

import java.time.ZonedDateTime;

public interface SimpleBasicBgMethod<T extends SimpleBasicBgWorker<T>>
{
    SimpleBasicBgResult<T> run(T worker);

    SimpleBasicBgResult<T> run(T worker, Throwable reasons, ZonedDateTime endtime);
}

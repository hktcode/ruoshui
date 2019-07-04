/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;

import java.time.ZonedDateTime;

public interface SimpleMethodPut<T extends BgWorker<T, M>, M>
{
    SimpleMethodPutResult<T, M> run(T worker, M metric);
}

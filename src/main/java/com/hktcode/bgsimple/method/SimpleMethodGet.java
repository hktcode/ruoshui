/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;

public interface SimpleMethodGet<T extends BgWorker<T, M>, M>
{
    SimpleMethodGetResult<T, M> run(T worker, M metric);
}

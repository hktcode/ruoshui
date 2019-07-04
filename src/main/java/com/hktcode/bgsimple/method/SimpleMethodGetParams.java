/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;

public interface SimpleMethodGetParams<T extends BgWorker<T, M>, M> //
    extends SimpleMethodGet<T, M>
{
    @Override
    SimpleMethodGetResult<T, M> run(T worker, M metric);
}

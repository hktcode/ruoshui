/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;

public interface SimpleMethodPutParams<T extends BgWorker<T, M>, M> //
    extends SimpleMethodPut<T, M>
{
    @Override
    SimpleMethodPutResult<T, M> run(T worker, M metric);
}

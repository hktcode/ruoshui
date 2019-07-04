/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;

public interface SimpleMethodDel<T extends BgWorker<T, M>, M>
{
    SimpleMethodDelResult<T, M> run(T worker, M metric);
}

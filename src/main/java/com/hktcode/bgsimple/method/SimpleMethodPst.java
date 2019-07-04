/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;

public interface SimpleMethodPst<T extends BgWorker<T, M>, M>
{
    SimpleMethodPstResult<T, M> run(T worker, M metric);
}

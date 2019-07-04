/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;

public interface SimpleMethodPstParams<T extends BgWorker<T, M>, M> //
    extends SimpleMethodPst<T, M>
{
    @Override
    SimpleMethodPstResult<T, M> run(T worker, M metric);
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;

public interface SimpleMethodDelParams<T extends BgWorker<T, M>, M> //
    extends SimpleMethodDel<T, M>
{
    @Override
    SimpleMethodDelResult<T, M> run(T worker, M metric);
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;

public interface SimpleMethodDelParams<W extends BgWorker<W, M>, M> //
    extends SimpleMethodDel<W, M>
{
    @Override
    SimpleMethodDelResult<W, M> run(W worker, M metric);
}

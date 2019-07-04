/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;

public interface SimpleMethodPutParams<W extends BgWorker<W, M>, M> //
    extends SimpleMethodPut<W, M>
{
    @Override
    SimpleMethodPutResult<W, M> run(W worker, M metric);
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleMethodGetParams<W extends BgWorker<W, M>, M> //
    extends SimpleMethodGet<W, M>
{
    @Override
    SimpleMethodGetResult<W, M> run(W worker, M metric);
}

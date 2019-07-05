/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleMethodGet<W extends BgWorker<W, M>, M>
{
    SimpleMethodGetResult<W, M> run(W worker, M metric);
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;

public interface SimpleMethodPst<W extends BgWorker<W, M>, M>
{
    SimpleMethodPstResult<W, M> run(W worker, M metric);
}

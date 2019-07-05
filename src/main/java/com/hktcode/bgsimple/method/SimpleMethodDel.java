/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleMethodDel<W extends BgWorker<W, M>, M>
{
    SimpleMethodDelResult<W, M> run(W worker, M metric);
}

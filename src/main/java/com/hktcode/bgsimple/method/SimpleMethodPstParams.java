/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleMethodPstParams<W extends BgWorker<W, M>, M> //
    extends SimpleMethodPst<W, M>
{
    @Override
    SimpleMethodPstResult<W, M> run(W worker, M metric);
}

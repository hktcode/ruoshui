/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleMethodPstParams<W extends BgWorker<W>> //
    extends SimpleMethodPst<W>
{
    @Override
    SimpleMethodPstResult<W> run(W worker) throws InterruptedException;
}

/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleMethodPst<W extends BgWorker<W>> extends SimpleMethod<W>
{
    SimpleMethodPstResult<W> run(W worker) throws InterruptedException;
}

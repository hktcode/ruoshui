/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleMethodGet<W extends BgWorker<W>> extends SimpleMethod<W>
{
    SimpleMethodGetResult<W> run(W wkstep) throws InterruptedException;
}

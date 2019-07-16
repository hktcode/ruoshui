/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleMethodGetParams<W extends BgWorker<W>> //
    extends SimpleMethodGet<W>
{
    @Override
    SimpleMethodGetResult<W> run(W wkstep);
}

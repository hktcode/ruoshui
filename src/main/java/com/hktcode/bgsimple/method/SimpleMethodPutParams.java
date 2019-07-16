/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleMethodPutParams<W extends BgWorker<W>> //
    extends SimpleMethodPut<W>
{
    @Override
    SimpleMethodPutResult<W> run(W wkstep);
}

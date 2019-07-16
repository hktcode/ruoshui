/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;

public interface SimpleMethodDelParams<W extends BgWorker<W>> //
    extends SimpleMethodDel<W>
{
    @Override
    SimpleMethodDelResult<W> run(W wkstep);
}

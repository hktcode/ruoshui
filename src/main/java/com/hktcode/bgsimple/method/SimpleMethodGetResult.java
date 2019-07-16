/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public interface SimpleMethodGetResult<W extends BgWorker<W>> //
    extends SimpleMethodGet<W>
{
    @Override
    default SimpleMethodGetResult<W> run(W wkstep)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        return this;
    }
}

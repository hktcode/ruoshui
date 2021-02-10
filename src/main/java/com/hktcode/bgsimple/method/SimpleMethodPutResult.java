/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public interface SimpleMethodPutResult<W extends BgWorker<W>> //
    extends SimpleMethodPut<W>, SimpleMethodResult<W>
{
    @Override
    default SimpleMethodPutResult<W> run(W wkstep)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        return this;
    }
}

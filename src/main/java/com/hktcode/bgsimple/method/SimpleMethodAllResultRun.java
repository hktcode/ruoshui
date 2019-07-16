/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public interface SimpleMethodAllResultRun<W extends BgWorker<W>> //
    extends SimpleMethodAllResult<W> //
{
    @Override
    default SimpleMethodAllResultRun<W> run(W wkstep)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        return this;
    }
}

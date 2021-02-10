/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public interface SimpleMethodResult extends SimpleMethod
{
    @Override
    default SimpleMethodResult run(BgWorker wkstep)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        // TODO: logger this should not be happened
        return this;
    }
}

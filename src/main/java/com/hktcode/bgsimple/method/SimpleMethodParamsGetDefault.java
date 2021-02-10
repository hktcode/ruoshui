/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleMethodParamsGetDefault implements SimpleMethodParams
{
    public static SimpleMethodParamsGetDefault of()
    {
        return new SimpleMethodParamsGetDefault();
    }

    @Override
    public SimpleMethodResult run(BgWorker wkstep) throws InterruptedException
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        return wkstep.get();
    }
}

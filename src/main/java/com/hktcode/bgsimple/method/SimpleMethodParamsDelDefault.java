/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleMethodParamsDelDefault implements SimpleMethodParams
{
    public static SimpleMethodParamsDelDefault of()
    {
        return new SimpleMethodParamsDelDefault();
    }

    private SimpleMethodParamsDelDefault()
    {
    }

    @Override
    public SimpleMethodResult run(BgWorker wkstep) throws InterruptedException
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        return wkstep.del();
    }
}

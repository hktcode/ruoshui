/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleMethodParamsPstDefault implements SimpleMethodParams
{
    public static SimpleMethodParamsPstDefault of()
    {
        return new SimpleMethodParamsPstDefault();
    }

    private SimpleMethodParamsPstDefault()
    {
    }

    @Override
    public SimpleMethodResult run(BgWorker wkstep) throws InterruptedException
    {
        if (wkstep == null) {
            throw new ArgumentNullException("worker");
        }
        return wkstep.pst();
    }
}

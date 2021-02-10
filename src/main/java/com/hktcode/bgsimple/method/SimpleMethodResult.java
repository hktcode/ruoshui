/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SimpleMethodResult extends SimpleMethod
{
    Logger logger = LoggerFactory.getLogger(SimpleMethodResult.class);

    @Override
    default SimpleMethodResult run(BgWorker wkstep)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        logger.error("this should not be happened.");
        return this;
    }
}

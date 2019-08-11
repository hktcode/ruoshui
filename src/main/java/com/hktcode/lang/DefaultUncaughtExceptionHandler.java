/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultUncaughtExceptionHandler //
    implements Thread.UncaughtExceptionHandler
{
    private static final Logger logger = LoggerFactory.getLogger(DefaultUncaughtExceptionHandler.class);

    public static DefaultUncaughtExceptionHandler of()
    {
        return new DefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        long id = t.getId();
        String name = t.getName();
        boolean isDaemon = t.isDaemon();
        logger.error("uncaught exception throws by thread: id={}, name={}, isDaemon={}" //
            , id, name, isDaemon, e);
    }
}

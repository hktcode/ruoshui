/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgtriple.naive;

public class NaiveConfig
{
    public static final long DEFALUT_WAIT_TIMEOUT = 100;

    public static final long DEFAULT_LOG_DURATION = 10 * 1000L;

    public long waitTimeout;

    public long logDuration;

    // TODO: loggerLevel
    protected NaiveConfig(long waitTimeout, long logDuration)
    {
        this.waitTimeout = waitTimeout;
        this.logDuration = logDuration;
    }
}

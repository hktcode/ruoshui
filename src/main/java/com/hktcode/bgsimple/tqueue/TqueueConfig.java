/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.tqueue;

public class TqueueConfig
{
    public static final long DEFALUT_WAIT_TIMEOUT = 100;

    public static final long DEFAULT_LOG_DURATION = 10 * 1000L;

    public long waitTimeout = DEFALUT_WAIT_TIMEOUT;

    public long logDuration = DEFAULT_LOG_DURATION;
}

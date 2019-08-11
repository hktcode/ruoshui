/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.tqueue;

import com.hktcode.bgsimple.triple.TripleConfig;

public class TqueueConfig
{
    public long waitTimeout = TripleConfig.DEFALUT_WAIT_TIMEOUT;

    public long logDuration = TripleConfig.DEFAULT_LOG_DURATION;
}

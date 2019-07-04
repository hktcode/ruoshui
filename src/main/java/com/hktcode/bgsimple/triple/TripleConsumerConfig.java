/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public class TripleConsumerConfig extends TripleConfig
{
    protected TripleConsumerConfig(long waitTimeout, long logDuration)
    {
        super(waitTimeout, logDuration);
    }
}

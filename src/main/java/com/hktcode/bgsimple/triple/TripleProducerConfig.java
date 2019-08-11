/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple;

public class TripleProducerConfig extends TripleConfig
{
    protected TripleProducerConfig(long waitTimeout, long logDuration)
    {
        super();
        this.waitTimeout = waitTimeout;
        this.logDuration = logDuration;
    }
}

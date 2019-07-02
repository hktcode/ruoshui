/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgtriple.kafka;

import com.google.common.collect.ImmutableMap;
import com.hktcode.bgtriple.naive.NaiveProducerConfig;

public class KafkaProducerConfig extends NaiveProducerConfig
{
    public final ImmutableMap<String, String> kfkProperty;

    protected KafkaProducerConfig
        /* */( long waitTimeout
        /* */, long logDuration
        /* */, ImmutableMap<String, String> kfkProperty
        /* */)
    {
        super(waitTimeout, logDuration);
        this.kfkProperty = kfkProperty;
    }
}

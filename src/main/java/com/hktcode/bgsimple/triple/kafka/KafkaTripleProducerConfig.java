/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple.kafka;

import com.google.common.collect.ImmutableMap;
import com.hktcode.bgsimple.triple.TripleProducerConfig;

public class KafkaTripleProducerConfig extends TripleProducerConfig
{
    public final ImmutableMap<String, String> kfkProperty;

    protected KafkaTripleProducerConfig
        /* */( long waitTimeout
        /* */, long logDuration
        /* */, ImmutableMap<String, String> kfkProperty
        /* */)
    {
        super(waitTimeout, logDuration);
        this.kfkProperty = kfkProperty;
    }
}

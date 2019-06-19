/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgtriple.kafka;

import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.naive.NaiveProducerMutableMetric;
import com.hktcode.bgtriple.status.TripleBasicBgStatus;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class KafkaCallbackProducer
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends KafkaBasicProducer<C, J, P, F, M, O, K, V> //
    /* */, F extends KafkaProducerConfig //
    /* */, M extends NaiveProducerMutableMetric //
    /* */, O //
    /* */, K
    /* */, V
    /* */> //
    extends KafkaBasicProducer<C, J, P, F, M, O, K, V>
{
    protected KafkaCallbackProducer //
        /* */( F config
        /* */, M metric
        /* */, BlockingQueue<O> getout
        /* */, AtomicReference<TripleBasicBgStatus<C, J, P>> status
        /* */)
    {
        super(config, metric, getout, status);
    }
}

/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.bgtriple.kafka;

import com.hktcode.bgtriple.TripleConsumer;
import com.hktcode.bgtriple.TripleJunction;
import com.hktcode.bgtriple.naive.NaiveProducer;
import com.hktcode.bgtriple.naive.NaiveProducerMutableMetric;
import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class KafkaBasicProducer //
    /* */< C extends TripleConsumer<C, J, P> //
    /* */, J extends TripleJunction<C, J, P> //
    /* */, P extends KafkaBasicProducer<C, J, P, F, M, O, K, V> //
    /* */, F extends KafkaProducerConfig //
    /* */, M extends NaiveProducerMutableMetric //
    /* */, O
    /* */, K
    /* */, V
    /* */> //
    extends NaiveProducer<C, J, P, F, M, O>
{
    private static final Logger logger = LoggerFactory.getLogger(KafkaBasicProducer.class);

    protected KafkaBasicProducer //
        /* */( F config //
        /* */, M metric //
        /* */, BlockingQueue<O> getout //
        /* */, AtomicReference<TripleBasicBgStatus<C, J, P>> status //
        /* */)
    {
        super(config, metric, getout, status);
    }

    protected Producer<K, V> producer(Serializer<K> k, Serializer<V> v)
    {
        Properties properties = new Properties();
        for (Map.Entry<String, String> e : config.kfkProperty.entrySet()) {
            properties.setProperty(e.getKey(), e.getValue());
        }
        return new KafkaProducer<>(properties, k, v);
    }
}

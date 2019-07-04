/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.triple.kafka;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.triple.TripleProducer;
import com.hktcode.bgsimple.triple.TripleProducerMetric;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class KafkaTripleProducer //
    /* */< W extends KafkaTripleProducer<W, F, M, O, K, V> //
    /* */, F extends KafkaTripleProducerConfig //
    /* */, M extends TripleProducerMetric //
    /* */, O
    /* */, K
    /* */, V
    /* */> //
    extends TripleProducer<W, F, M, O>
{
    private static final Logger logger = LoggerFactory.getLogger(KafkaTripleProducer.class);

    protected KafkaTripleProducer //
        /* */( F config //
        /* */, BlockingQueue<O> getout //
        /* */, AtomicReference<SimpleStatus<W, M>> status //
        /* */)
    {
        super(config, getout, status);
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

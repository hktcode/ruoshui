/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.kafka;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;

/**
 * immutable Producer Record.
 *
 * @param <K> the type of key.
 * @param <V> the type of value.
 */
public class ImmutableProducerRecord<K, V>
{
    /**
     * Obtain an ImmutableProducerRecord from a topic, partition, headers,
     * key, value and timestamp.
     *
     * @param topic The topic the record will be appended to.
     * @param partition The partition to which the record should be sent.
     * @param headers the headers that will be included in the record.
     * @param key The key that will be included in the record
     * @param value the record contents.
     * @param timestamp The timestamp of the record, in milliseconds since epoch. If less than 0, the producer will assign.
     * @param <K> the type of key.
     * @param <V> the type of value.
     *
     * @return an ImmutableProducerRerord object.
     *
     * @throws ArgumentNullException if {@code topic}, {@code headers}, {@code key} or {@code value} is {@code null}.
     */
    public static <K, V> ImmutableProducerRecord<K, V> of //
        /* */ ( String topic //
        /* */, long partition //
        /* */, ImmutableList<Header> headers //
        /* */, K key //
        /* */, V value //
        /* */, long timestamp //
        /* */)
    {
        if (topic == null) {
            throw new ArgumentNullException("topic");
        }
        if (headers == null) {
            throw new ArgumentNullException("headers");
        }
        if (key == null) {
            throw new ArgumentNullException("key");
        }
        if (value == null) {
            throw new ArgumentNullException("value");
        }
        return new ImmutableProducerRecord<>(topic, partition, headers, key, value, timestamp);
    }

    /**
     * Constructor.
     *
     * @param topic The topic the record will be appended to.
     * @param partition The partition to which the record should be sent.
     *                  If less than 0, it will auto assign a prtition.
     * @param headers the headers that will be included in the record.
     * @param key The key that will be included in the record.
     * @param value the record contents.
     * @param timestamp The timestamp of the record, in milliseconds since epoch.
     *                 If less than 0, the producer will assign.
     */
    private ImmutableProducerRecord //
        /* */( String topic //
        /* */, long partition
        /* */, ImmutableList<Header> headers
        /* */, K key
        /* */, V value
        /* */, long timestamp
        /* */)
    {
        this.topic = topic;
        this.partition = partition;
        this.headers = headers;
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }

    /**
     * The topic the record will be appended to.
     */
    public final String topic;

    /**
     * The partition to which the record should be sent.
     *
     * If less than 0, it will auto assign a prtition.
     */
    public final long partition;

    /**
     * the headers that will be included in the record.
     */
    public final ImmutableList<Header> headers;

    /**
     * The key that will be included in the record.
     */
    public final K key;

    /**
     * the record contents.
     */
    public final V value;

    /**
     * The timestamp of the record, in milliseconds since epoch.
     *
     * If less than 0, the producer will assign.
     */
    public final long timestamp; // 小于0表示没有
}

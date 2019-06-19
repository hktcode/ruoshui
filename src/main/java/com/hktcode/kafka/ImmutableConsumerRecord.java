/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.kafka;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.record.TimestampType;

/**
 * Immutable Consumer Record.
 *
 * @param <K> the type of key.
 * @param <V> the type of value.
 */
public class ImmutableConsumerRecord<K, V>
{
    /**
     * Obtain a ImmutableConsumerRecord from ConsumerRecord.
     *
     * @param record the special ConsumerRecord.
     * @param <K> the type of key.
     * @param <V> the type of value.
     *
     * @return a ImmutableConsumerRecord Object.
     * @throws ArgumentNullException if {@code record} is {@code null}.
     */
    public static <K, V> //
    ImmutableConsumerRecord<K, V> of(ConsumerRecord<K, V> record)
    {
        if (record == null) {
            throw new ArgumentNullException("record");
        }
        return new ImmutableConsumerRecord<> //
            /* */( record.topic() //
            /* */, record.partition() //
            /* */, record.offset() //
            /* */, record.timestamp() //
            /* */, record.timestampType() //
            /* */, record.serializedKeySize() //
            /* */, record.serializedValueSize() //
            /* */, ImmutableList.copyOf(record.headers()) //
            /* */, record.key() //
            /* */, record.value() //
            /* */);
    }

    /**
     * Obtain an ImmutableConsumerRecord from fields.
     *
     * @param topic The topic this record is received from
     * @param partition The partition of the topic this record is received from
     * @param offset The offset of this record in the corresponding Kafka partition.
     * @param timestamp the timestamp of the consumer record.
     * @param timestampType the timesatmp type.
     * @param serializedKeySize The length of the serialized key.
     * @param serializedValueSize The length of the serialized value.
     * @param headers the headers of record.
     * @param key the key of record, null is not allowed.
     * @param value the record content, null is not allowed.
     * @param <K> the type of key.
     * @param <V> the type of value.
     * @return an ImmutableConsumerRecord Object.
     * @throws ArgumentNullException if {@code topic}, {@code timestampType}, {@code headers}, {@code key} or {@code value} is null.
     */
    public static <K, V> ImmutableConsumerRecord<K, V> of //
        /* */( String topic
        /* */, long partition
        /* */, long offset
        /* */, long timestamp
        /* */, TimestampType timestampType
        /* */, long serializedKeySize
        /* */, long serializedValueSize
        /* */, ImmutableList<Header> headers
        /* */, K key
        /* */, V value
        /* */)
    {
        if (topic == null) {
            throw new ArgumentNullException("topic");
        }
        if (timestampType == null) {
            throw new ArgumentNullException("timestampType");
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
        // TODO: topic format
        return new ImmutableConsumerRecord //
            /* */( topic //
            /* */, partition //
            /* */, offset //
            /* */, timestamp //
            /* */, timestampType //
            /* */, serializedKeySize //
            /* */, serializedValueSize //
            /* */, headers //
            /* */, key //
            /* */, value //
            /* */);

    }

    /**
     * The topic this record is received from
     */
    public final String topic;

    /**
     * The partition from which this record is received
     */
    public final long partition;

    /**
     * The position of this record in the corresponding Kafka partition.
     */
    public final long offset;

    /**
     * The timestamp of this record
     */
    public final long timestamp;

    /**
     * The timestamp type of this record.
     */
    public final TimestampType timestampType;

    /**
     * The size of the serialized, uncompressed key in bytes.
     */
    public final long serializedKeySize;

    /**
     * The size of the serialized, uncompressed value in bytes.
     */
    public final long serializedValueSize;

    /**
     * The headers
     */
    public final ImmutableList<Header> headers;

    /**
     * The key.
     */
    public final K key;

    /**
     * The value.
     */
    public final V value;

    /**
     * put information to {@link ObjectNode}.
     *
     * @param node the {@link ObjectNode} which hold the information.
     */
    public void putMetadata(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node.put("topic", this.topic);
        node.put("partition", this.partition);
        node.put("offset", this.offset);
        node.put("timestamp", this.timestamp);
        node.put("timestampType", this.timestampType.name);
        node.put("serializedKeySize", this.serializedKeySize);
        node.put("serializedValueSize", this.serializedValueSize);
        ArrayNode headersNode = node.putArray("headers");
        for (Header header : this.headers) {
            ObjectNode headerNode = headersNode.addObject();
            headerNode.put(header.key(), header.value());
        }
    }

    /**
     * constructor
     *
     * @param topic The topic this record is received from
     * @param partition The partition of the topic this record is received from
     * @param offset The offset of this record in the corresponding Kafka partition.
     * @param timestamp the timestamp of the consumer record.
     * @param timestampType the timesatmp type.
     * @param serializedKeySize The length of the serialized key.
     * @param serializedValueSize The length of the serialized value.
     * @param headers the headers of record.
     * @param key the key of record, null is not allowed.
     * @param value the record content, null is not allowed.
     */
    private ImmutableConsumerRecord
        /* */( String topic
        /* */, long partition
        /* */, long offset
        /* */, long timestamp
        /* */, TimestampType timestampType
        /* */, long serializedKeySize
        /* */, long serializedValueSize
        /* */, ImmutableList<Header> headers
        /* */, K key
        /* */, V value
        /* */)
    {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.timestamp = timestamp;
        this.timestampType = timestampType;
        this.serializedKeySize = serializedKeySize;
        this.serializedValueSize = serializedValueSize;
        this.headers = headers;
        this.key = key;
        this.value = value;
    }
}

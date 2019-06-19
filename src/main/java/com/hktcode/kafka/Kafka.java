/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.kafka;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;

import java.util.regex.Pattern;

/**
 * the util of Kakfa.
 */
public final class Kafka
{
    /**
     * the pattern of kafka topic.
     */
    public static final Pattern TOPIC_PATTERN = Pattern.compile("(:?\\w|[.-])+");

    /**
     * the predefined Deserializers.
     */
    public static final class Deserializers
    {
        /**
         * the deserializer of long type.
         */
        public static final LongDeserializer LONG = new LongDeserializer();

        /**
         * the deserializer of byte array.
         */
        public static final ByteArrayDeserializer BYTES = new ByteArrayDeserializer();
    }

    /**
     * the predefined serializers.
     */
    public static final class Serializers
    {
        /**
         * the serializer of long type.
         */
        public static final LongSerializer LONG = new LongSerializer();

        /**
         * the serializer of byte array.
         */
        public static final ByteArraySerializer BYTES = new ByteArraySerializer();
    }
}

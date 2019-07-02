/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hktcode.kafka.Kafka;
import com.hktcode.bgtriple.kafka.KafkaProducerConfig;
import com.hktcode.lang.exception.ArgumentIllegalException;
import com.hktcode.lang.exception.ArgumentNullException;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.HashMap;
import java.util.Map;

import static com.hktcode.pgstack.Ruoshui.THE_NAME;

public class UpperProducerConfig extends KafkaProducerConfig
{
    public static UpperProducerConfig ofJsonObject(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        long waitTimeout = json.path("wait_timeout").asLong(DEFALUT_WAIT_TIMEOUT);
        long logDuration = json.path("log_duration").asLong(DEFAULT_LOG_DURATION);
        Map<String, String> kfkMap = UpperProducerConfig.createDefaultMap();
        JsonNode kfkNode = json.get("kfk_property");
        if (kfkNode != null) {
            UpperConfig.merge(kfkMap, kfkNode);
        }
        ImmutableMap<String, String> kfkProperty = ImmutableMap.copyOf(kfkMap);
        // TODO: 检查properties

        String targetTopic = UpperProducerConfig.DEFAULT_TARGET_TOPIC;
        targetTopic = json.path("target_topic").asText(targetTopic);
        if (!Kafka.TOPIC_PATTERN.matcher(targetTopic).matches()) {
            throw new ArgumentIllegalException("topic name is not match the pattern", "targetTopic", targetTopic); // TODO:
        }

        int partitionNo = UpperProducerConfig.DEFAULT_PARTITION_NO;
        partitionNo = json.path("partition_no").asInt(partitionNo);
        if (partitionNo < 0) {
            throw new ArgumentIllegalException //
                /*    */( "argument is less than zero" //
                , "partitionNo" //
                , partitionNo  //
            ); // TODO:
        }
        return new UpperProducerConfig(waitTimeout, kfkProperty, targetTopic, partitionNo, logDuration);
    }

    public static final String DEFAULT_TARGET_TOPIC = THE_NAME;
    public static final int DEFAULT_PARTITION_NO = 0;

    public static final ImmutableSet<String> PROPERTY_KEYS = ImmutableSet.of //
        /*   */ ( ProducerConfig.BATCH_SIZE_CONFIG //
            , ProducerConfig.BOOTSTRAP_SERVERS_CONFIG //
            , ProducerConfig.BUFFER_MEMORY_CONFIG //
            , ProducerConfig.COMPRESSION_TYPE_CONFIG //
            , ProducerConfig.LINGER_MS_CONFIG //
            , ProducerConfig.MAX_BLOCK_MS_CONFIG //
            , ProducerConfig.MAX_REQUEST_SIZE_CONFIG //
        );

    public static Map<String, String> createDefaultMap()
    {
        Map<String, String> result = new HashMap<>();
        result.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return result;
    }

    public final String targetTopic;

    public final int partitionNo;

    private UpperProducerConfig //
        /* */( long waitTimeout
        /* */, ImmutableMap<String, String> kfkProperty //
        /* */, String targetTopic //
        /* */, int partitionNo //
        /* */, long logDuration //
        /* */)
    {
        super(waitTimeout, logDuration, kfkProperty);
        this.targetTopic = targetTopic;
        this.partitionNo = partitionNo;
    }
}

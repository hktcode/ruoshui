/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.hktcode.simple.SimpleConfig;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.kafka.Kafka;
import com.hktcode.lang.exception.ArgumentIllegalException;
import com.hktcode.lang.exception.ArgumentNegativeException;
import com.hktcode.lang.exception.ArgumentNullException;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.HashMap;
import java.util.Map;

import static com.hktcode.ruoshui.Ruoshui.THE_NAME;

public class UppdcConfig extends SimpleConfig
{
    public final static ObjectNode SCHEMA = JacksonObject.getFromResource(UppdcConfig.class, "UppdcConfig.yml");

    public static UppdcConfig ofJsonObject(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        // wait_timeout
        // log_duration
        // factory_type
        // config_props
        //   kfk_property
        //   target_topic
        //   partition_no
        // config_props
        //   wal_datapath
        //   max_synctime
        //   max_syncsize
        //   max_filesize
        //   max_filetime
        //
        //
        long waitTimeout = json.path("wait_timeout").asLong(DEFALUT_WAIT_TIMEOUT);
        long logDuration = json.path("log_duration").asLong(DEFAULT_LOG_DURATION);
        Map<String, String> kfkMap = UppdcConfig.createDefaultMap();
        JsonNode kfkNode = json.get("kfk_property");
        if (kfkNode != null) {
            JacksonObject.merge(kfkMap, kfkNode);
        }
        ImmutableMap<String, String> kfkProperty = ImmutableMap.copyOf(kfkMap);
        // TODO: 检查properties

        String targetTopic = UppdcConfig.DEFAULT_TARGET_TOPIC;
        targetTopic = json.path("target_topic").asText(targetTopic);
        if (!Kafka.TOPIC_PATTERN.matcher(targetTopic).matches()) {
            throw new ArgumentIllegalException("topic name is not match the pattern", "targetTopic", targetTopic); // TODO:
        }

        int partitionNo = UppdcConfig.DEFAULT_PARTITION_NO;
        partitionNo = json.path("partition_no").asInt(partitionNo);
        if (partitionNo < 0) {
            // TODO:
            throw new ArgumentNegativeException("partitionNo", partitionNo);
        }
        UppdcConfig result = new UppdcConfig(kfkProperty, targetTopic, partitionNo);
        result.waitTimeout = waitTimeout;
        result.logDuration = logDuration;
        return result;
    }

    public static final String DEFAULT_TARGET_TOPIC = THE_NAME;
    public static final int DEFAULT_PARTITION_NO = 0;

    private static Map<String, String> createDefaultMap()
    {
        Map<String, String> result = new HashMap<>();
        result.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return result;
    }

    public final ImmutableMap<String, String> kfkProperty;

    public final String targetTopic;

    public final int partitionNo;

    private UppdcConfig //
        /* */( ImmutableMap<String, String> kfkProperty //
        /* */, String targetTopic //
        /* */, int partitionNo //
        /* */)
    {
        this.kfkProperty = kfkProperty;
        this.targetTopic = targetTopic;
        this.partitionNo = partitionNo;
    }

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ObjectNode result = super.toJsonObject(node);
        ObjectNode kfkPropertyNode = node.putObject("kfk_property");
        for (Map.Entry<String, String> entry : this.kfkProperty.entrySet()) {
            kfkPropertyNode.put(entry.getKey(), entry.getValue());
        }
        result.put("target_topic", this.targetTopic);
        result.put("partition_no", this.partitionNo);
        return result;
    }
}

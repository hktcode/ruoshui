/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.kafka.Kafka;
import com.hktcode.lang.exception.ArgumentIllegalException;
import com.hktcode.lang.exception.ArgumentNegativeException;
import com.hktcode.lang.exception.ArgumentNullException;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.HashMap;
import java.util.Map;

import static com.hktcode.ruoshui.Ruoshui.THE_NAME;

public class UppdcWkstepArgvalKafka extends UppdcWkstepArgval
{
    public static UppdcWkstepArgvalKafka ofJsonObject(JsonNode json)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        Map<String, String> kfkMap = UppdcWkstepArgvalKafka.createDefaultMap();
        JsonNode kfkNode = json.get("kfk_property");
        if (kfkNode != null) {
            JacksonObject.merge(kfkMap, kfkNode);
        }
        ImmutableMap<String, String> kfkProperty = ImmutableMap.copyOf(kfkMap);
        // TODO: 检查properties

        String targetTopic = UppdcWkstepArgvalKafka.DEFAULT_TARGET_TOPIC;
        targetTopic = json.path("target_topic").asText(targetTopic);
        if (!Kafka.TOPIC_PATTERN.matcher(targetTopic).matches()) {
            throw new ArgumentIllegalException("topic name is not match the pattern", "targetTopic", targetTopic); // TODO:
        }

        int partitionNo = UppdcWkstepArgvalKafka.DEFAULT_PARTITION_NO;
        partitionNo = json.path("partition_no").asInt(partitionNo);
        if (partitionNo < 0) {
            // TODO:
            throw new ArgumentNegativeException("partitionNo", partitionNo);
        }
        return new UppdcWkstepArgvalKafka(kfkProperty, targetTopic, partitionNo);
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

    private UppdcWkstepArgvalKafka //
        /* */( ImmutableMap<String, String> kfkProperty //
        /* */, String targetTopic //
        /* */, int partitionNo //
        /* */)
    {
        super("kafka");
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
        ObjectNode configPropsNode = result.putObject("config_props");
        ObjectNode kfkPropertyNode = configPropsNode.putObject("kfk_property");
        for (Map.Entry<String, String> entry : this.kfkProperty.entrySet()) {
            kfkPropertyNode.put(entry.getKey(), entry.getValue());
        }
        configPropsNode.put("target_topic", this.targetTopic);
        configPropsNode.put("partition_no", this.partitionNo);
        return node;
    }

    @Override
    public UppdcSenderKafka sender()
    {
        return UppdcSenderKafka.of(this, UppdcWkstepGaugesKafka.of());
    }
}

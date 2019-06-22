/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.bgtriple.naive.NaiveJunctionConfig;
import com.hktcode.lang.exception.ArgumentNullException;

import javax.script.ScriptException;
import java.util.Iterator;
import java.util.Map;

public class UpperConfig
{
    public static UpperConfig of(JsonNode jsonNode) throws ScriptException
    {
        if (jsonNode == null) {
            throw new ArgumentNullException("jsonNode");
        }
        UpperConsumerConfig consumer = UpperConsumerConfig.ofJsonObject(jsonNode.path("consumer"));
        NaiveJunctionConfig junction = NaiveJunctionConfig.ofJsonObject(jsonNode.path("junction"));
        UpperProducerConfig producer = UpperProducerConfig.ofJsonObject(jsonNode.path("producer"));

        return new UpperConfig(consumer, junction, producer);
    }

    public static void merge(Map<String, String> map, JsonNode objectNode)
    {
        Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields();
        while(it.hasNext()) {
            Map.Entry<String, JsonNode> e = it.next();
            map.put(e.getKey(), e.getValue().asText());
        }
    }

    public final UpperConsumerConfig consumer;

    public final NaiveJunctionConfig junction;

    public final UpperProducerConfig producer;

    private UpperConfig //
        /* */(UpperConsumerConfig consumer //
        /* */, NaiveJunctionConfig junction //
        /* */, UpperProducerConfig producer //
        /* */)
    {
        this.consumer = consumer;
        this.junction = junction;
        this.producer = producer;
    }
}

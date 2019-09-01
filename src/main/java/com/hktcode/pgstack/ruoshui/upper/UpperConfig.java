/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.bgsimple.triple.TripleJunctionConfig;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;
import com.hktcode.pgstack.ruoshui.upper.producer.UpperProducerConfig;

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
        MainlineConfig consumer = MainlineConfig.ofJsonObject(jsonNode.path("consumer"));
        TripleJunctionConfig junction = TripleJunctionConfig.ofJsonObject(jsonNode.path("junction"));
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

    public final MainlineConfig consumer;

    public final TripleJunctionConfig junction;

    public final UpperProducerConfig producer;

    private UpperConfig //
        /* */( MainlineConfig consumer //
        /* */, TripleJunctionConfig junction //
        /* */, UpperProducerConfig producer //
        /* */)
    {
        this.consumer = consumer;
        this.junction = junction;
        this.producer = producer;
    }
}

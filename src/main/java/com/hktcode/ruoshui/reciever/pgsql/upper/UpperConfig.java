package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.TqueueConfig;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmConfig;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctConfig;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcConfig;

public class UpperConfig
{
    public static final ObjectNode SCHEMA;

    static
    {
        ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        schema.put("type", "object");
        ObjectNode propertiesNode = schema.putObject("properties");
        propertiesNode.set("consumer", UpcsmConfig.SCHEMA);
        propertiesNode.set("srcqueue", TqueueConfig.SCHEMA);
        propertiesNode.set("junction", UpjctConfig.SCHEMA);
        propertiesNode.set("tgtqueue", TqueueConfig.SCHEMA);
        propertiesNode.set("producer", UppdcConfig.SCHEMA);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UpperConfig ofJsonObject(JsonNode jsonNode)
    {
        if (jsonNode == null) {
            throw new ArgumentNullException("jsonNode");
        }
        UpcsmConfig consumer = UpcsmConfig.ofJsonObject(jsonNode.path("consumer"));
        TqueueConfig srcqueue = TqueueConfig.ofJsonObject(jsonNode.path("srcqueue"));
        UpjctConfig junction = UpjctConfig.ofJsonObject(jsonNode.path("junction"));
        TqueueConfig tgtqueue = TqueueConfig.ofJsonObject(jsonNode.path("tgtqueue"));
        UppdcConfig producer = UppdcConfig.ofJsonObject(jsonNode.path("producer"));
        return new UpperConfig(consumer, srcqueue, junction, tgtqueue, producer);
    }

    public final UpcsmConfig consumer; // laborer
    public final TqueueConfig srcqueue;
    public final UpjctConfig junction;
    public final TqueueConfig tgtqueue;
    public final UppdcConfig producer;

    private UpperConfig //
            /* */( UpcsmConfig consumer //
            /* */, TqueueConfig srcqueue //
            /* */, UpjctConfig junction //
            /* */, TqueueConfig tgtqueue //
            /* */, UppdcConfig producer //
            /* */)
    {
        this.consumer = consumer;
        this.srcqueue = srcqueue;
        this.junction = junction;
        this.tgtqueue = tgtqueue;
        this.producer = producer;
    }
}

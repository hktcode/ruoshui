package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.TqueueConfig;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmArgval;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctArgval;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcArgval;

public class UpperConfig
{
    public static final ObjectNode SCHEMA;

    static
    {
        ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        schema.put("type", "object");
        ObjectNode propertiesNode = schema.putObject("properties");
        propertiesNode.set("consumer", UpcsmArgval.SCHEMA);
        propertiesNode.set("srcqueue", TqueueConfig.SCHEMA);
        propertiesNode.set("junction", UpjctArgval.SCHEMA);
        propertiesNode.set("tgtqueue", TqueueConfig.SCHEMA);
        propertiesNode.set("producer", UppdcArgval.SCHEMA);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UpperConfig ofJsonObject(JsonNode jsonNode)
    {
        if (jsonNode == null) {
            throw new ArgumentNullException("jsonNode");
        }
        UpcsmArgval consumer = UpcsmArgval.ofJsonObject(jsonNode.path("consumer"));
        TqueueConfig srcqueue = TqueueConfig.ofJsonObject(jsonNode.path("srcqueue"));
        UpjctArgval junction = UpjctArgval.ofJsonObject(jsonNode.path("junction"));
        TqueueConfig tgtqueue = TqueueConfig.ofJsonObject(jsonNode.path("tgtqueue"));
        UppdcArgval producer = UppdcArgval.ofJsonObject(jsonNode.path("producer"));
        return new UpperConfig(consumer, srcqueue, junction, tgtqueue, producer);
    }

    public final UpcsmArgval consumer; // laborer
    public final TqueueConfig srcqueue;
    public final UpjctArgval junction;
    public final TqueueConfig tgtqueue;
    public final UppdcArgval producer;

    private UpperConfig //
            /* */( UpcsmArgval consumer //
            /* */, TqueueConfig srcqueue //
            /* */, UpjctArgval junction //
            /* */, TqueueConfig tgtqueue //
            /* */, UppdcArgval producer //
            /* */)
    {
        this.consumer = consumer;
        this.srcqueue = srcqueue;
        this.junction = junction;
        this.tgtqueue = tgtqueue;
        this.producer = producer;
    }
}

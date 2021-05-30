package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.TqueueConfig;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmWorkerArgval;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctWorkerArgval;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcWorkerArgval;

public class UpperExesvcArgval implements JacksonObject
{
    public static final ObjectNode SCHEMA;

    static
    {
        ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        schema.put("type", "object");
        ObjectNode propertiesNode = schema.putObject("properties");
        propertiesNode.set("consumer", UpcsmWorkerArgval.SCHEMA);
        propertiesNode.set("srcqueue", TqueueConfig.SCHEMA);
        propertiesNode.set("junction", UpjctWorkerArgval.SCHEMA);
        propertiesNode.set("tgtqueue", TqueueConfig.SCHEMA);
        propertiesNode.set("producer", UppdcWorkerArgval.SCHEMA);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UpperExesvcArgval ofJsonObject(String fullname, JsonNode jsonnode)
    {
        if (fullname == null) {
            throw new ArgumentNullException("fullname");
        }
        if (jsonnode == null) {
            throw new ArgumentNullException("jsonnode");
        }
        UpcsmWorkerArgval consumer = UpcsmWorkerArgval.ofJsonObject(jsonnode.path("consumer"));
        TqueueConfig srcqueue = TqueueConfig.ofJsonObject(jsonnode.path("srcqueue"));
        UpjctWorkerArgval junction = UpjctWorkerArgval.ofJsonObject(jsonnode.path("junction"));
        TqueueConfig tgtqueue = TqueueConfig.ofJsonObject(jsonnode.path("tgtqueue"));
        UppdcWorkerArgval producer = UppdcWorkerArgval.ofJsonObject(jsonnode.path("producer"));
        return new UpperExesvcArgval(fullname, consumer, srcqueue, junction, tgtqueue, producer);
    }

    public final String fullname;
    public final UpcsmWorkerArgval consumer; // laborer
    public final TqueueConfig srcqueue;
    public final UpjctWorkerArgval junction;
    public final TqueueConfig tgtqueue;
    public final UppdcWorkerArgval producer;

    private UpperExesvcArgval //
            /* */( String fullname //
            /* */, UpcsmWorkerArgval consumer //
            /* */, TqueueConfig srcqueue //
            /* */, UpjctWorkerArgval junction //
            /* */, TqueueConfig tgtqueue //
            /* */, UppdcWorkerArgval producer //
            /* */)
    {
        this.fullname = fullname;
        this.consumer = consumer;
        this.srcqueue = srcqueue;
        this.junction = junction;
        this.tgtqueue = tgtqueue;
        this.producer = producer;
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        ObjectNode c = node.putObject("consumer");
        this.consumer.toJsonObject(c);
        ObjectNode s = node.putObject("srcqueue");
        this.srcqueue.toJsonObject(s);
        ObjectNode j = node.putObject("junction");
        this.junction.toJsonObject(j);
        ObjectNode t = node.putObject("tgtqueue");
        this.tgtqueue.toJsonObject(t);
        ObjectNode p = node.putObject("producer");
        this.producer.toJsonObject(p);
        return node;
    }
}

package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmWorkerArgval;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctWorkerArgval;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcWorkerArgval;

public class UpperHolderArgval implements JacksonObject
{
    public static final ObjectNode SCHEMA;

    static
    {
        ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        schema.put("type", "object");
        ObjectNode propertiesNode = schema.putObject("properties");
        propertiesNode.set("consumer", UpcsmWorkerArgval.SCHEMA);
        propertiesNode.set("junction", UpjctWorkerArgval.SCHEMA);
        propertiesNode.set("producer", UppdcWorkerArgval.SCHEMA);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UpperHolderArgval ofJsonObject(String fullname, JsonNode jsonnode)
    {
        if (fullname == null) {
            throw new ArgumentNullException("fullname");
        }
        if (jsonnode == null) {
            throw new ArgumentNullException("jsonnode");
        }
        UpcsmWorkerArgval consumer = UpcsmWorkerArgval.ofJsonObject(jsonnode.path("consumer"));
        Xqueue<UpperRecordConsumer> fetchXqueue = consumer.offerXqueue;
        UpjctWorkerArgval junction = UpjctWorkerArgval.ofJsonObject(jsonnode.path("junction"), fetchXqueue);
        Xqueue<UpperRecordProducer> offerXqueue = junction.offerXqueue;
        UppdcWorkerArgval producer = UppdcWorkerArgval.ofJsonObject(jsonnode.path("producer"), offerXqueue);
        return new UpperHolderArgval(fullname, consumer, junction, producer);
    }

    public final String fullname;
    public final UpcsmWorkerArgval consumer; // laborer
    public final UpjctWorkerArgval junction;
    public final UppdcWorkerArgval producer;

    private UpperHolderArgval //
            /* */(String fullname //
            /* */, UpcsmWorkerArgval consumer //
            /* */, UpjctWorkerArgval junction //
            /* */, UppdcWorkerArgval producer //
            /* */)
    {
        this.fullname = fullname;
        this.consumer = consumer;
        this.junction = junction;
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
        ObjectNode j = node.putObject("junction");
        this.junction.toJsonObject(j);
        ObjectNode p = node.putObject("producer");
        this.producer.toJsonObject(p);
        return node;
    }
}

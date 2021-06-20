package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleWorkerArgval;

public class UpjctWorkerArgval implements SimpleWorkerArgval<UpjctWorkerArgval, UpjctWorkerGauges>
{
    public static final ObjectNode SCHEMA;

    static
    {
        ObjectNode schema = new ObjectNode(JsonNodeFactory.instance);
        schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        schema.put("type", "object");
        ObjectNode argvalNode = schema.putObject("properties");
        ObjectNode actionInfosNode = argvalNode.putObject("action_infos");
        actionInfosNode.put("type", "array");
        // - actionInfosNode.set("items", UpjctWkstepArgval.SCHEMA);
        actionInfosNode.put("maxItems", 1);
        SCHEMA = JacksonObject.immutableCopy(schema);
    }

    public static UpjctWorkerArgval ofJsonObject(JsonNode json, Xqueue<UpperRecordConsumer> recver) //
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (recver == null) {
            throw new ArgumentNullException("recver");
        }
        Xqueue<UpperRecordProducer> sender = Xqueue.of(json.path("sender"));
        UpjctWorkerArgval result = new UpjctWorkerArgval(recver, sender);
        result.xspins.pst(json.path("xspins"));
        return result;
    }

    public final Xqueue.Spins xspins = Xqueue.Spins.of();
    public final Xqueue<UpperRecordConsumer> recver;
    public final Xqueue<UpperRecordProducer> sender;

    private UpjctWorkerArgval(Xqueue<UpperRecordConsumer> recver, Xqueue<UpperRecordProducer> sender)
    {
        this.recver = recver;
        this.sender = sender;
    }

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        return node;
    }

    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
    }

    @Override
    public UpjctWkstepAction action()
    {
        return UpjctWkstepAction.of();
    }
}
